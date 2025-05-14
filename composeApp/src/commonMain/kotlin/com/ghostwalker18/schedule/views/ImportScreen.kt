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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ghostwalker18.schedule.widgets.ListView
import com.ghostwalker18.schedule.ScheduleApp
import com.ghostwalker18.schedule.platform.ImportController
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.data_types
import scheduledesktop2.composeapp.generated.resources.import_activity
import scheduledesktop2.composeapp.generated.resources.operation_type

/**
 * Эта функция представляет собой экран импорта и экспорта данных приложения
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
fun ImportScreen(){
    val navigator = ScheduleApp.instance.getNavigator()
    val controller = ScheduleApp.instance.importController
    val scaffoldState = rememberScaffoldState()
    controller.initController()
    val scope = rememberCoroutineScope()
    val operationStatus by controller.status.collectAsState()
    if(operationStatus != ImportController.OperationStatus.Ready){
        scope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                when(operationStatus){
                    ImportController.OperationStatus.Started -> getString(Res.string.starting_import)
                    ImportController.OperationStatus.Packing -> getString(Res.string.packing_export_dp)
                    ImportController.OperationStatus.Unpacking -> getString(Res.string.unpacking_import_db)
                    ImportController.OperationStatus.Doing -> getString(Res.string.doing_import)
                    ImportController.OperationStatus.Ended -> getString(Res.string.import_success)
                    ImportController.OperationStatus.Error -> getString(Res.string.import_failed)
                    else -> "unknown"
                }
            )
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.import_activity)) },
                navigationIcon = {
                    IconButton({ navigator.goBack() }){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.go_back_descr)
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(it) { data ->
                Snackbar(
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.primaryVariant,
                    snackbarData = data
                )
            }
        }
    ) {
        var operationType by remember { mutableStateOf("export") }
        var dataType by remember { mutableStateOf("schedule") }
        var importMode by remember { mutableStateOf("replace") }
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 5.dp)
            ){
                Text(
                    text = stringResource(Res.string.operation_type),
                    modifier = Modifier
                        .weight(0.5f)
                )
                ListView(
                    entries = Res.array.operation_type_entries,
                    entryValues = Res.array.operation_type_values,
                    modifier = Modifier.weight(0.5f)
                ){
                    operationType = it
                }
            }
            Row(
                modifier = Modifier
                    .padding(vertical = 5.dp)
            ){
                Text(
                    text = stringResource(Res.string.data_types),
                    modifier = Modifier
                        .weight(0.5f)
                )
                ListView(
                    entries = Res.array.data_types_entries,
                    entryValues = Res.array.data_types_values,
                    modifier = Modifier.weight(0.5f)
                ){
                    dataType = it
                }
            }
            AnimatedVisibility(operationType == "import"){
                Row(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                ){
                    Text(
                        text = stringResource(Res.string.import_policy_type),
                        modifier = Modifier
                            .weight(0.5f)
                    )
                    ListView(
                        entries = Res.array.import_mode_entries,
                        entryValues = Res.array.import_mode_values,
                        modifier = Modifier.weight(0.5f)
                    ){
                        importMode = it
                    }
                }
            }
            Spacer(Modifier.weight(0.5f))
            Button(
                {
                    controller.importPolicy = importMode
                    controller.dataType = dataType
                    if (operationType == "import")
                        controller.importDB()
                    else
                        controller.exportDB()
                },
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Text(
                    text = if (operationType == "import")
                        stringResource(Res.string.import_data)
                    else
                        stringResource(Res.string.export_data)
                )
            }
        }
    }
}