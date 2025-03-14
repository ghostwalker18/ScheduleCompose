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

import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.runtime.Composable
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

    Column{
        Row{
            IconButton({
                galleryPickLauncher.launch("image/*")
            }){
                Icon(Icons.Filled.PhotoLibrary, null)
            }
            IconButton({
                cameraPermissionLauncher.launch("")
            }){
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