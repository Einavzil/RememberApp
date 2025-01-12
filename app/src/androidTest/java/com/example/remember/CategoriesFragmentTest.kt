package com.example.remember

import android.view.WindowManager
import android.widget.EditText
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Root
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CategoriesFragmentTest {

    private lateinit var dbHelper: DatabaseHelper

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        dbHelper = DatabaseHelper(context, "remember.db") // Use in-memory database


    }

    @After
    fun tearDown() {
        dbHelper.close()
    }

    @Test
    fun testCategoriesDisplayedInRecyclerView() {
        // Launch the fragment
        val scenario = launchFragmentInContainer<CategoriesFragment>(
            themeResId = R.style.Theme_Remember
        )
        // The database comes with pre-loaded categories, no need to insert additional ones
        val categories = dbHelper.getCategories()
        // Assert: Check that categories are displayed in the RecyclerView
        for (category in categories) {
            onView(withText(category.name)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun testAddNewCategory() {
        // Launch the fragment
        val scenario = launchFragmentInContainer<CategoriesFragment>(
            themeResId = R.style.Theme_Remember
        )
        val categories = dbHelper.getCategories()
        // Click on the FAB to open the create category dialog
        onView(withId(R.id.floatingActionButton)).perform(click())

        // Enter a new category name and save
        onView(isAssignableFrom(EditText::class.java)).perform(typeText("Personal"), closeSoftKeyboard())
        onView(withText("Save")).perform(click())

        // Assert: Check that the new category is displayed in the RecyclerView
        onView(withText("Personal")).check(matches(isDisplayed()))
        // Assert: The category is added to the database
        val newCategories = dbHelper.getCategories()
        assertEquals(categories.count()+1,newCategories.count())
        assertNotNull(newCategories.find { x->x.name=="Personal" })
    }

    @Test
    fun testEditCategory() {
        // Launch the fragment
        val scenario = launchFragmentInContainer<CategoriesFragment>(
            themeResId = R.style.Theme_Remember
        )
        val categories = dbHelper.getCategories()
        // Click on an existing category to open the edit dialog
        onView(withText("Travel")).perform(click())

        // Update the category name and save
        val perform = onView(isAssignableFrom(EditText::class.java)).perform(
            replaceText("Test"),
            closeSoftKeyboard()
        )
        onView(withText("Save")).perform(click())

        // Assert: Check that the updated category name is displayed
        onView(withText("Test")).check(matches(isDisplayed()))
        // Assert: Check that the number of categories on db is unchanged
        val newCategories = dbHelper.getCategories()
        assertEquals(categories.count(),newCategories.count())
        // Assert: Check that the category with updated title exists on db
        assertNotNull(newCategories.find { x->x.name == "Test" })
    }

    // This test will check the number of categories before and after saving, to make sure that no empty category is created
    @Test
    fun testPreventEmptyCategory() {
        // Launch the fragment
        val scenario = launchFragmentInContainer<CategoriesFragment>(
            themeResId = R.style.Theme_Remember
        )
        val categories = dbHelper.getCategories()
        // Click the FAB to open the create dialog
        onView(withId(R.id.floatingActionButton)).perform(click())

        // Try to save without entering a category name
        onView(withText("Save")).perform(click())

        // Assert: Verify that the number of categories is unchanged
        val newCategories = dbHelper.getCategories()
        assertEquals(categories.count(),newCategories.count())
    }

}

class ToastMatcher : TypeSafeMatcher<Root>() {
    override fun describeTo(description: Description) {
        description.appendText("is a Toast")
    }

    override fun matchesSafely(root: Root): Boolean {
        val type = root.windowLayoutParams?.get()?.type
        return type == WindowManager.LayoutParams.TYPE_TOAST
    }
}