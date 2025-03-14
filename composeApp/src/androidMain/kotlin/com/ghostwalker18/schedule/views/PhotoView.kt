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

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.net.toUri
import kotlinx.coroutines.launch
import java.util.*

@Composable
actual fun PhotoView(
    photoIDs: MutableList<String>,
    isEditable: Boolean,
    onDeleteListener: (id: String) -> Unit
){
    val pagerState = rememberPagerState { photoIDs.size }
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Text(
                text = String.format(
                    Locale("ru"),
                    "%d/%d",
                    pagerState.currentPage, pagerState.pageCount
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            if(isEditable){
                IconButton({
                    if(photoIDs.isNotEmpty()) {
                        photoIDs.removeAt(pagerState.currentPage)
                    }
                }){
                    Icon(Icons.Filled.Delete, null)
                }
            }
        }
        HorizontalPager(pagerState){
            page ->
            val uri = photoIDs[page].toUri()
            uri.encodedPath?.let {
                val bitmap = BitmapFactory.decodeFile(it).asImageBitmap()
                Image(bitmap, null)
            }
        }
        Row{
            IconButton({
                scope.launch {
                    pagerState.animateScrollToPage(0)
                }
            }){
                Icon(Icons.Filled.FirstPage, null)
            }
            IconButton({
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }){
                Icon(Icons.AutoMirrored.Filled.ArrowLeft, null)
            }
            IconButton({
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            }){
                Icon(Icons.AutoMirrored.Filled.ArrowRight,null)
            }
            IconButton({
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.pageCount)
                }
            }){
                Icon(Icons.AutoMirrored.Filled.LastPage,null)
            }
        }
    }
}