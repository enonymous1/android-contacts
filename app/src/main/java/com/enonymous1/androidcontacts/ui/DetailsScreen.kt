package com.enonymous1.androidcontacts.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.enonymous1.androidcontacts.PeopleViewModel

/**
 * Details for one person, loaded by id from the database. The original
 * details screen never received the selected name — it was passed as
 * activity options instead of intent extras — and its getPerson() returned
 * null, so it showed hardcoded placeholder text and its Delete button did
 * nothing. This screen cannot see another person's data, and delete removes
 * exactly this row by id.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    viewModel: PeopleViewModel,
    personId: Long,
    onBack: () -> Unit,
    onDeleted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val person by viewModel.person(personId).collectAsState(initial = null)
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog && person != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Person") },
            text = { Text("Are you sure you want to delete \"${person!!.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deletePerson(person!!)
                        onDeleted()
                    },
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            },
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Person details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete person",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (person == null) {
                Text(text = "Person not found.")
            } else {
                Text(text = "Name", style = MaterialTheme.typography.titleMedium)
                Text(text = person!!.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Phone", style = MaterialTheme.typography.titleMedium)
                Text(text = person!!.phone, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Date of birth", style = MaterialTheme.typography.titleMedium)
                Text(text = person!!.dob, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
