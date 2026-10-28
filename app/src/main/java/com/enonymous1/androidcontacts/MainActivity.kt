package com.enonymous1.androidcontacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.enonymous1.androidcontacts.data.PeopleDatabase
import com.enonymous1.androidcontacts.data.RoomPeopleRepository
import com.enonymous1.androidcontacts.ui.ContactsApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = Room.databaseBuilder(
            applicationContext,
            PeopleDatabase::class.java,
            "contacts.db",
        ).build()
        val viewModel = ViewModelProvider(
            this,
            PeopleViewModelFactory(RoomPeopleRepository(database.peopleDao())),
        )[PeopleViewModel::class.java]
        setContent {
            MaterialTheme {
                ContactsApp(viewModel)
            }
        }
    }
}
