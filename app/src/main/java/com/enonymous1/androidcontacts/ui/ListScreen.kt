package com.enonymous1.androidcontacts.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.enonymous1.androidcontacts.model.Person

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    people: List<Person>,
    onAddPerson: () -> Unit,
    onPersonClick: (Person) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Contacts") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPerson) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add person",
                )
            }
        },
    ) { innerPadding ->
        if (people.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No people yet. Add a person and they will appear here.",
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items = people, key = { it.id }) { person ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPersonClick(person) },
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = person.name,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = person.phone,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                text = person.dob,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}
