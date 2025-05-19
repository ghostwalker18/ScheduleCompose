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
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.LastPage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FirstPage
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.ghostwalker18.schedule.ScheduleApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.preview_image_clear_descr
import scheduledesktop2.composeapp.generated.resources.preview_next_descr
import scheduledesktop2.composeapp.generated.resources.preview_previous_descr
import java.util.*

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
actual fun PhotoPreview(
    modifier: Modifier,
    photoIDs: List<String>,
    isEditable: Boolean,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    onDeleteListener: (id: String) -> Unit
){
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { photoIDs.size }
    val bitmaps = remember { mutableStateMapOf<String, ImageBitmap>() }
    val context = LocalContext.current

    scope.launch(
        Dispatchers.IO
    ){
        photoIDs.forEach{
            try{
                if(!bitmaps.containsKey(it)){
                    bitmaps[it] = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(
                            context.contentResolver, it.toUri()
                        ).asImageBitmap()
                    } else {
                        val source = ImageDecoder.createSource(
                            context.contentResolver, it.toUri()
                        )
                        ImageDecoder.decodeBitmap(source).asImageBitmap()
                    }
                }
            } catch (_: Exception){/* Not required */ }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(pagerState.pageCount > 0){
                AnimatedContent(
                    targetState = pagerState.currentPage,
                    transitionSpec = {
                        if(targetState > initialState){
                            slideInVertically { height -> -height } + fadeIn() togetherWith
                                    slideOutVertically { height -> height } + fadeOut()
                        }
                        else {
                            slideInVertically { height -> height } + fadeIn() togetherWith
                                    slideOutVertically { height -> -height } + fadeOut()
                        }.using(
                            SizeTransform(clip = false)
                        )
                    }
                ){
                    targetState ->
                    Text(text = (targetState + 1).toString())
                }
                Text(
                    text = String.format(
                        Locale("ru"),
                        "/%d",
                        pagerState.pageCount
                    )
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            AnimatedVisibility(
                visible = isEditable && pagerState.pageCount > 0,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ){
                IconButton({
                    if(photoIDs.isNotEmpty()) {
                        onDeleteListener(photoIDs[pagerState.currentPage])
                    }
                }){
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(Res.string.preview_image_clear_descr)
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = pagerState.pageCount > 0,
            enter = fadeIn() + expandVertically(
                expandFrom = Alignment.CenterVertically
            ),
            exit = fadeOut() + shrinkVertically(
                shrinkTowards = Alignment.CenterVertically
            )
        ){
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ){
                page ->
                bitmaps[photoIDs[page]]?.let {
                    with(sharedTransitionScope!!){
                        Image(
                            bitmap = it,
                            modifier = Modifier
                                .sharedElement(
                                    state = rememberSharedContentState(photoIDs[page]),
                                    animatedVisibilityScope = animatedVisibilityScope!!
                                ).clickable {
                                    ScheduleApp.instance.getNavigator().goPhotoView(photoIDs[page])
                                }
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            contentDescription = stringResource(Res.string.preview_photo_descr)
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = pagerState.pageCount > 1,
            enter = fadeIn(),
            exit = fadeOut()
        ){
            Row{
                AnimatedVisibility(
                    visible = pagerState.pageCount > 2,
                    enter = fadeIn(),
                    exit = fadeOut()
                ){
                    IconButton(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        },
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                    ){
                        Icon(
                            imageVector = Icons.Filled.FirstPage,
                            contentDescription = stringResource(Res.string.preview_first_descr)
                        )
                    }
                }
                IconButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                ){
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowLeft,
                        contentDescription = stringResource(Res.string.preview_previous_descr)
                    )
                }
                IconButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                ){
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                        contentDescription = stringResource(Res.string.preview_next_descr)
                    )
                }
                AnimatedVisibility(
                    visible = pagerState.pageCount > 2,
                    enter = fadeIn(),
                    exit = fadeOut()
                ){
                    IconButton(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.pageCount)
                            }
                        },
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                    ){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.LastPage,
                            contentDescription = stringResource(Res.string.preview_last_descr)
                        )
                    }
                }
            }
        }
    }
}