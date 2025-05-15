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

import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.preview_photo_descr
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.photoview_back_descr
import scheduledesktop2.composeapp.generated.resources.photoview_share_descr

/**
 * Минимальный масштаб изображения
 */
const val minScale = 1f

/**
 * Максимальный масштаб изображения
 */
const val maxScale = 2.5f

/**
 * Эта функция отображает фото из заметки в полноэкранном режиме.
 *
 * @author Ипатов Никита
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PhotoViewScreen(
    photoID: String,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    onBackPressed: () -> Unit
){
    val bitmap = if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images.Media.getBitmap(
            LocalContext.current.contentResolver, photoID.toUri()
        ).asImageBitmap()
    } else {
        val source = ImageDecoder.createSource(
            LocalContext.current.contentResolver, photoID.toUri()
        )
        ImageDecoder.decodeBitmap(source).asImageBitmap()
    }
    var scale by remember { mutableStateOf(1f) }
    val state = rememberTransformableState { zoomChange, _, _ ->
        scale *= zoomChange
    }
    val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()

    @Composable
    fun ActionsPanel(){
        Column(
            modifier = Modifier
                .fillMaxSize()
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(
                    onClick = onBackPressed,
                    modifier = Modifier
                        .padding(5.dp)
                        .size(48.dp)
                ){
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = Color.White,
                        contentDescription = stringResource(Res.string.photoview_back_descr)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .padding(5.dp)
                        .size(48.dp)
                ){
                    Icon(
                        imageVector = Icons.Filled.Share,
                        tint = Color.White,
                        contentDescription = stringResource(Res.string.photoview_share_descr)
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }

    @Composable
    fun ImagePanel(){
        Column(
            modifier = Modifier
                .fillMaxSize()
        ){
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable { scale = 1f }
            )
            with(sharedTransitionScope!!){
                val scaleFactor =
                    if(scale > minScale && scale <= maxScale)
                        scale
                    else if(scale > maxScale)
                        maxScale
                    else
                        minScale
                Image(
                    bitmap = bitmap,
                    modifier = Modifier
                        .graphicsLayer(
                            scaleX = scaleFactor,
                            scaleY = scaleFactor
                        )
                        .transformable(state = state)
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio)
                        .pointerInput(
                            key1 = null
                        ){
                            detectTapGestures(
                                onDoubleTap = {
                                    scale =
                                        if(scale !=  minScale)
                                            minScale
                                        else
                                            maxScale
                                }
                            )
                        }
                        .sharedElement(
                            state = rememberSharedContentState(photoID),
                            animatedVisibilityScope = animatedVisibilityScope!!
                        ),
                    contentDescription = stringResource(Res.string.preview_photo_descr)
                )
            }
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable { scale = 1f }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        ImagePanel()
        ActionsPanel()
    }
}