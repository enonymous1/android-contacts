package com.enonymous1.androidcontacts

import com.enonymous1.androidcontacts.data.PeopleRepository
import com.enonymous1.androidcontacts.model.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class FakePeopleRepository : PeopleRepository {
    private val state = MutableStateFlow<List<Person>>(emptyList())
    override val people: Flow<List<Person>> = state
    private var nextId = 1L

    override fun observePerson(id: Long): Flow<Person?> =
        state.map { list -> list.find { it.id == id } }

    override suspend fun add(person: Person) {
        state.value = state.value + person.copy(id = nextId++)
    }

    override suspend fun delete(person: Person) {
        state.value = state.value.filterNot { it.id == person.id }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PeopleViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun addingAValidPersonAppearsInTheList() = runTest {
        val viewModel = PeopleViewModel(FakePeopleRepository())
        assertTrue(viewModel.addPerson("Ada", "555-555-5555", "1990-01-01").isEmpty())
        advanceUntilIdle()
        assertEquals(1, viewModel.people.value.size)
        val person = viewModel.people.value.single()
        assertEquals("Ada", person.name)
        assertEquals("555-555-5555", person.phone)
        assertEquals("1990-01-01", person.dob)
    }

    @Test
    fun invalidAddReturnsErrorsAndAddsNothing() = runTest {
        val viewModel = PeopleViewModel(FakePeopleRepository())
        val errors = viewModel.addPerson("Ada", "555", "1990-01-01")
        assertTrue(errors.isNotEmpty())
        advanceUntilIdle()
        assertTrue(viewModel.people.value.isEmpty())
    }

    @Test
    fun deletingRemovesOnlyThePersonWithThatId() = runTest {
        // Two people share a name. The original saved one file per name, so
        // the second overwrote the first; deleting one must never take the other.
        val viewModel = PeopleViewModel(FakePeopleRepository())
        viewModel.addPerson("Ada", "555-555-5555", "1990-01-01")
        viewModel.addPerson("Ada", "555-555-5556", "1991-01-01")
        advanceUntilIdle()
        assertEquals(2, viewModel.people.value.size)
        viewModel.deletePerson(viewModel.people.value.first())
        advanceUntilIdle()
        assertEquals(listOf("555-555-5556"), viewModel.people.value.map { it.phone })
    }

    @Test
    fun deletingTheLastPersonEmptiesTheList() = runTest {
        val viewModel = PeopleViewModel(FakePeopleRepository())
        viewModel.addPerson("Ada", "555-555-5555", "1990-01-01")
        advanceUntilIdle()
        viewModel.deletePerson(viewModel.people.value.single())
        advanceUntilIdle()
        assertTrue(viewModel.people.value.isEmpty())
    }
}
