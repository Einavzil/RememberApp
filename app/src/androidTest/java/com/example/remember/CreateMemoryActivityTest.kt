package com.example.remember

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.remember.models.CategoryModel
import com.example.remember.models.MemoryModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateMemoryActivityTest {


    @get:Rule
    val activityRule = ActivityScenarioRule(CreateMemoryActivity::class.java)

    @Before
    fun setUp() {
        // Initialize Intents
        Intents.init()
    }

    @After
    fun tearDown() {
        // Release Intents
        Intents.release()
    }

    // Test the behavior of the activity when updating an existing memory
    @Test
    fun testSaveButtonClick_updateMemory() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dbHelper = DatabaseHelper(context, "remember.db")

        // Prepopulate database with a memory
        val initialMemory = MemoryModel(
            id = 0, // SQLite will auto-generate the ID
            title = "Old Vacation",
            description = "A description",
            category = CategoryModel(1, "Travel"),
            image = "/path/to/image"
        )
        val insertedId = dbHelper.insertNewMemory(initialMemory).toInt()

        // Create an intent with the memoryId as an extra
        val intent = Intent(context, CreateMemoryActivity::class.java).apply {
            putExtra("memoryId", insertedId)
        }

        // Launch activity with the intent
        val scenario = ActivityScenario.launch<CreateMemoryActivity>(intent)

        // Update memory inputs
        onView(withId(R.id.title_input)).perform(clearText(), typeText("Updated Vacation"), closeSoftKeyboard())
        onView(withId(R.id.description_input)).perform(clearText(), typeText("Updated description"), closeSoftKeyboard())
        onView(withId(R.id.save_button)).perform(click())

        // Assert: Verify the memory was updated in the database
        val updatedMemory = dbHelper.getOneMemory(insertedId)
        assertEquals("Updated Vacation", updatedMemory?.title)
        assertEquals("Updated description", updatedMemory?.description)
    }
    @Test
    fun testSaveButtonClick_validationErrors() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Attempt to save with empty fields
        onView(withId(R.id.save_button)).perform(click())

        // Assert: Check for validation error messages on the title
        onView(withId(R.id.title_input)).check(matches(hasErrorText(context.getString(R.string.title_empty))))

        // Insert a title
        onView(withId(R.id.title_input))
            .perform(clearText(), replaceText("Thailand Vacation"), closeSoftKeyboard())

        // Try to save without a description
        onView(withId(R.id.save_button)).perform(click())
        // Assert: Check for validation error messages on the description
        onView(withId(R.id.description_input)).check(matches(hasErrorText(context.getString(R.string.description_empty))))
        // Insert description
        onView(withId(R.id.description_input)).perform(typeText("Had an amazing time exploring!"), closeSoftKeyboard())
        // Try to save without a category
        onView(withId(R.id.save_button)).perform(click())
        // Assert: Check for validation error messages on the category
        onView(withId(R.id.category_dropdown)).check(matches(hasErrorText(context.getString(R.string.category_not_selected))))
    }
    @Test
    fun testSaveButtonClick_addMemory() {
        // Arrange: Set up user inputs for title, description, category, and image
        onView(withId(R.id.title_input))
            .perform(clearText(), replaceText("Thailand Vacation"), closeSoftKeyboard())
        onView(withId(R.id.description_input)).perform(typeText("Had an amazing time exploring!"), closeSoftKeyboard())
        onView(withId(R.id.category_dropdown)).perform(click())
        onView(allOf(
            isAssignableFrom(android.widget.TextView::class.java),
            withText("Travel"),
            isDisplayed()
        )).inRoot(isPlatformPopup()).perform(click())

        // Act: Simulate clicking the save button
        onView(withId(R.id.save_button)).perform(click())

        // Assert: Verify the memory is added to the database
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dbHelper = DatabaseHelper(context, "remember.db")
        val memories = dbHelper.getMemories(true)
        val savedMemory = memories.find { it.title == "Thailand Vacation" }

        assertNotNull(savedMemory)
        assertEquals("Had an amazing time exploring!", savedMemory?.description)
        assertEquals("Travel", savedMemory?.category?.name)
    }

    // test that the pickImage button launches an intent with type image/*
    @Test
    fun testPickImageIntent() {
        // Act
        onView(withId(R.id.upload_image_button)).perform(click())

        // Assert
        intended(IntentMatchers.hasAction(Intent.ACTION_PICK))
        intended(IntentMatchers.hasType("image/*"))
    }
}