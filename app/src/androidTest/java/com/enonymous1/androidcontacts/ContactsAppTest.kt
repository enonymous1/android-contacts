package com.enonymous1.androidcontacts

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.enonymous1.androidcontacts.data.PeopleDatabase
import com.enonymous1.androidcontacts.data.RoomPeopleRepository
import com.enonymous1.androidcontacts.ui.ContactsApp
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Runs on a device or emulator only; the GitHub Actions workflow runs the
 * JVM unit tests and a debug build, not these tests.
 */
@RunWith(AndroidJUnit4::class)
class ContactsAppTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun launch() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val database = Room.inMemoryDatabaseBuilder(context, PeopleDatabase::class.java).build()
        val viewModel = PeopleViewModel(RoomPeopleRepository(database.peopleDao()))
        composeRule.setContent {
            MaterialTheme { ContactsApp(viewModel) }
        }
    }

    private fun addPerson(name: String, phone: String, dob: String) {
        composeRule.onNodeWithContentDescription("Add person").performClick()
        composeRule.onNodeWithTag("nameField").performTextInput(name)
        composeRule.onNodeWithTag("phoneField").performTextInput(phone)
        composeRule.onNodeWithTag("dobField").performTextInput(dob)
        composeRule.onNodeWithTag("saveButton").performClick()
    }

    @Test
    fun emptyStateIsShownBeforeAnythingIsAdded() {
        launch()
        composeRule.onNodeWithText("No people yet. Add a person and they will appear here.")
            .assertIsDisplayed()
    }

    @Test
    fun addingAPersonShowsNamePhoneAndDobInTheList() {
        launch()
        addPerson("Ada", "555-555-5555", "1990-01-01")
        composeRule.onNodeWithText("Ada").assertIsDisplayed()
        composeRule.onNodeWithText("555-555-5555").assertIsDisplayed()
        composeRule.onNodeWithText("1990-01-01").assertIsDisplayed()
    }

    @Test
    fun emptyFieldsShowValidationErrorsInsteadOfAdding() {
        launch()
        composeRule.onNodeWithContentDescription("Add person").performClick()
        composeRule.onNodeWithTag("saveButton").performClick()
        composeRule.onNodeWithText("Name is required.").assertIsDisplayed()
        composeRule.onNodeWithText("Phone is required.").assertIsDisplayed()
        composeRule.onNodeWithText("Date of birth is required.").assertIsDisplayed()
    }

    @Test
    fun invalidPhoneAndDobShowValidationErrorsInsteadOfAdding() {
        launch()
        addPerson("Ada", "555", "00/00/0000")
        composeRule.onNodeWithText("Phone must contain 7 to 15 digits.").assertIsDisplayed()
        composeRule.onNodeWithText("Date of birth must be a valid date (YYYY-MM-DD).")
            .assertIsDisplayed()
    }

    @Test
    fun tappingAPersonOpensDetailsShowingEveryField() {
        launch()
        addPerson("Ada", "555-555-5555", "1990-01-01")
        composeRule.onNodeWithText("Ada").performClick()
        composeRule.onNodeWithText("Person details").assertIsDisplayed()
        composeRule.onNodeWithText("555-555-5555").assertIsDisplayed()
        composeRule.onNodeWithText("1990-01-01").assertIsDisplayed()
    }

    @Test
    fun deletingFromDetailsReturnsToTheEmptyList() {
        launch()
        addPerson("Ada", "555-555-5555", "1990-01-01")
        composeRule.onNodeWithText("Ada").performClick()
        composeRule.onNodeWithContentDescription("Delete person").performClick()
        composeRule.onNodeWithText("Delete").performClick()
        composeRule.onNodeWithText("No people yet. Add a person and they will appear here.")
            .assertIsDisplayed()
    }
}
