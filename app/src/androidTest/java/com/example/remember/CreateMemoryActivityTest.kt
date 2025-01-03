package com.example.remember

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class CreateMemoryActivityTest {
    @get:Rule
    val activityRule = ActivityTestRule(CreateMemoryActivity::class.java, false, false)

    @Mock
    lateinit var mockDatabaseHelper: DatabaseHelper

    @Before
    fun setUp() {

        // Initialize Intents
        Intents.init()
        MockitoAnnotations.openMocks(this)
    }


    @After
    fun tearDown() {
        // Release Intents
        Intents.release()
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