package com.enonymous1.androidcontacts.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.enonymous1.androidcontacts.PeopleViewModel

/**
 * The add-person form. Draft text survives rotation (rememberSaveable), and
 * saving validates before anything is stored — the original saved whatever
 * the form sent, under a filename taken from the name, with no validation.
 * The date of birth is typed as YYYY-MM-DD, the format the original HTML
 * date input produced; there is no date-picker dialog, matching the
 * original's scope.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    viewModel: PeopleViewModel,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var dob by rememberSaveable { mutableStateOf("") }
    var errors by remember { mutableStateOf(emptyList<String>()) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Add person") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = "Name", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("nameField"),
            )
            Text(text = "Phone", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth().testTag("phoneField"),
            )
            Text(text = "Date of birth", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = dob,
                onValueChange = { dob = it },
                label = { Text("Date of birth (YYYY-MM-DD)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("dobField"),
            )
            errors.forEach { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = {
                    errors = viewModel.addPerson(name, phone, dob)
                    if (errors.isEmpty()) onSaved()
                },
                modifier = Modifier.fillMaxWidth().testTag("saveButton"),
            ) { Text("Save") }
        }
    }
}
