/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ghostwalker18.schedule.views

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ghostwalker18.schedule.ScheduleApp
import com.ghostwalker18.schedule.converters.DateConverters
import com.ghostwalker18.schedule.viewmodels.EditNoteModel
import java.io.File
import java.util.*
import kotlin.random.Random


@Composable
actual fun AttachNotePhotoView(){
    val context = LocalContext.current
    val model = viewModel { EditNoteModel() }
    var photoUri: String? = null

    val galleryPickLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.encodedPath?.let { model.addPhotoID(it) } }

    val takePhotoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ){
        _ ->
        photoUri?.let { model.addPhotoID(it) }
        MediaScannerConnection.scanFile(ScheduleApp.getInstance(),
            arrayOf(photoUri), arrayOf("image/jpeg"), null)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        granted ->
        if(granted){
            val directory = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                ).absoluteFile, "ScheduleNotes"
            )
            if (!directory.exists()) directory.mkdirs()
            var newFile = File(directory, makeNotePhotoName(model.date.value))
            while (newFile.exists())
                newFile = File(directory, makeNotePhotoName(model.date.value))
            photoUri = Uri.fromFile(newFile).encodedPath
            val contentUri: Uri = FileProvider.getUriForFile(
                ScheduleApp.getInstance(),
                "com.ghostwalker18.schedule.timefilesprovider", newFile
            )
            takePhotoLauncher.launch(contentUri)
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            IconButton(
                onClick = {
                    galleryPickLauncher.launch("image/*")
                },
                modifier = Modifier
                    .padding(end = 10.dp)
                    .background(MaterialTheme.colors.primary)
                    .weight(0.5f)
            ){
                Icon(Icons.Filled.PhotoLibrary, null)
            }
            IconButton(
                onClick = {
                    if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                        if (shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.CAMERA)) {
                            /*val toast = Toast.makeText(
                                context,
                                context.resources.getText(R.string.permission_for_photo), Toast.LENGTH_SHORT
                            )
                            toast.show()*/
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                },
                modifier = Modifier
                    .padding(start = 10.dp)
                    .background(MaterialTheme.colors.primary)
                    .weight(0.5f)
            ){
                Icon(Icons.Filled.AddAPhoto, null)
            }
        }
        PhotoView(isEditable = true)
    }
}

/**
 * Этот метод позволяет сгенерировать имя для сделанного фото для заметки.
 * @return имя файла для фото
 */
private fun makeNotePhotoName(noteDate: Calendar): String {
    var res = ""
    res = res + DateConverters.dateFormatPhoto.format(noteDate.time) + "_"
    res += Random.nextInt(10000)
    res += ".jpg"
    return res
}

