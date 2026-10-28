package com.enonymous1.androidcontacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.enonymous1.androidcontacts.data.PeopleRepository
import com.enonymous1.androidcontacts.model.Person
import com.enonymous1.androidcontacts.model.PersonValidation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Owns the people state. The list is a live flow from the repository, so
 * adding or deleting a person updates the list immediately, and nothing is
 * lost on rotation — the original re-read serialized files on every list
 * load, crashed on any non-person file, and finished the hosting activity
 * after every save.
 */
class PeopleViewModel(private val repository: PeopleRepository) : ViewModel() {

    val people: StateFlow<List<Person>> = repository.people
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun person(id: Long): Flow<Person?> = repository.observePerson(id)

    /** Returns validation errors, or an empty list when the person was queued for saving. */
    fun addPerson(name: String, phone: String, dob: String): List<String> {
        val errors = PersonValidation.validate(name, phone, dob)
        if (errors.isNotEmpty()) return errors
        val person = PersonValidation.create(name = name, phone = phone, dob = dob)
            ?: return errors
        viewModelScope.launch { repository.add(person) }
        return emptyList()
    }

    fun deletePerson(person: Person) {
        viewModelScope.launch { repository.delete(person) }
    }
}

class PeopleViewModelFactory(private val repository: PeopleRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PeopleViewModel::class.java)) {
            return PeopleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
