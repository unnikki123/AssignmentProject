package com.ukv.assignmentproject.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ukv.assignmentproject.viewmodel.ItemViewModel
import com.ukv.assignmentproject.data.model.ItemEntity
import com.ukv.assignmentproject.header.AppHeader
import com.ukv.assignmentproject.header.ScreenAppHeader

@Composable
fun ItemListScreen(viewModel: ItemViewModel, navController: NavHostController) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val updatedItemState = remember { mutableStateOf<ItemEntity?>(null) }
    val updatedNameState = remember { mutableStateOf("") }
    val updatedJsonState = remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    val keyValueList = remember { mutableStateListOf<Pair<String, String>>() }

    LaunchedEffect(updatedItemState.value) {
        updatedItemState.value?.let {
            keyValueList.clear()
            keyValueList.addAll(updatedJsonState.value.toList())
            if (keyValueList.isEmpty()) {
                keyValueList.add("" to "")
            }
        }
    }

    val isLoading = items.isEmpty() && error == null

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ScreenAppHeader(title = "Item List", onBackClick = { navController.popBackStack() })

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Text("Error: $error", color = Color.Red, modifier = Modifier.padding(8.dp))
            }

            else -> {
                LazyColumn {
                    items(items) { item ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Name: ${item.name}", style = MaterialTheme.typography.titleMedium)

                                val dataMap = if (!item.dataJson.isNullOrEmpty()) {
                                    parseDataJson(item.dataJson)
                                } else {
                                    emptyMap()
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                if (dataMap.isNotEmpty()) {
                                    dataMap.forEach { (key, value) ->
                                        Text("$key: $value", style = MaterialTheme.typography.bodyMedium)
                                    }
                                } else {
                                    Text(
                                        "No additional data available",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(onClick = { viewModel.deleteItem(item) },
                                        modifier = Modifier.background(MaterialTheme.colorScheme.primary)) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(onClick = {
                                        updatedItemState.value = item
                                        updatedNameState.value = item.name
                                        updatedJsonState.value = dataMap
                                    },
                                        modifier = Modifier.background(MaterialTheme.colorScheme.primary)) {
                                        Icon(Icons.Default.Edit, contentDescription = "Update")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Existing AlertDialog remains unchanged below...
    updatedItemState.value?.let { item ->
        AlertDialog(
            onDismissRequest = { updatedItemState.value = null },
            title = { Text("Edit Item") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = updatedNameState.value,
                        onValueChange = { updatedNameState.value = it },
                        label = { Text("Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Custom Fields:", style = MaterialTheme.typography.titleSmall)

                    keyValueList.forEachIndexed { index, (key, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedTextField(
                                value = key,
                                onValueChange = {
                                    keyValueList[index] = it to keyValueList[index].second
                                },
                                label = { Text("Key") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                            )

                            OutlinedTextField(
                                value = value,
                                onValueChange = {
                                    keyValueList[index] = keyValueList[index].first to it
                                },
                                label = { Text("Value") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            keyValueList.add("" to "")
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Add Field")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val updatedMap = keyValueList
                        .filter { it.first.isNotBlank() }
                        .associate { it.first to it.second }

                    val updatedDataJson =
                        if (updatedMap.isEmpty()) null else Gson().toJson(updatedMap)

                    val updatedItem = item.copy(
                        name = updatedNameState.value,
                        dataJson = updatedDataJson
                    )

                    viewModel.updateItem(updatedItem)
                    updatedItemState.value = null
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { updatedItemState.value = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}


fun parseDataJson(json: String?): Map<String, String> {
    // Ensure that the json string is not null or empty before trying to parse
    if (json.isNullOrEmpty()) {
        return emptyMap()  // Return empty map for null or empty JSON
    }
    val type = object : TypeToken<Map<String, String>>() {}.type
    return try {
        Gson().fromJson(json, type)
    } catch (e: Exception) {
        emptyMap()  // Return empty map if parsing fails
    }
}


