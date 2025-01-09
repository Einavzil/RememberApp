package com.example.remember

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.remember.models.CategoryModel
import com.example.remember.models.DatabaseHelper
import com.example.remember.models.MemoryModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseHelperTest {

    private lateinit var dbHelper: DatabaseHelper

    @Before
    fun dbHelperInit() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // use an in-memory database for testing
        dbHelper = DatabaseHelper(context, ":memory:")
    }

    @After
    fun closeDatabase() {
        dbHelper.close()
    }

    @Test
    fun insertOneMemory_shouldInsertToDatabase() {

        // initializing an instance to insert
        val memory = MemoryModel(
            id = 0,
            title = "Thailand 2024",
            description = "It was fun!",
            category = CategoryModel(1 , "Travel"),
            image = "path/to/image"
        )

        //insert a memory
        val id = dbHelper.insertNewMemory(memory).toInt()
        val retrieveMemory = dbHelper.getOneMemory(id)

        //Testing that the data from database is correct
        assertEquals("Thailand 2024", retrieveMemory?.title)
        assertEquals("It was fun!", retrieveMemory?.description)
        assertEquals("Travel", retrieveMemory?.category?.name)
        assertEquals("path/to/image", retrieveMemory?.image)
    }

    @Test
    fun deleteMemory_shouldRemoveTheCorrectMemory() {

        //create an instance and insert to database
        val memory = MemoryModel(
            id = -1,
            title = "Thailand 2024",
            description = "It was fun!",
            category = CategoryModel(1, "Travel"),
            image = "path/to/image"
        )
        val id = dbHelper.insertNewMemory(memory).toInt()

        //test that delete memory returns true and that the memory cannot be found
        val isDeleted = dbHelper.deleteMemory(id)
        assertTrue(isDeleted)

        val retrieveEmptyMemory = dbHelper.getOneMemory(id)
        assertNull(retrieveEmptyMemory)
    }

    @Test
    fun updateMemory_shouldUpdateTheCorrectColumns() {
        //create an instance and insert to database
        val memory = MemoryModel(
            id = -1,
            title = "Thailand 2024",
            description = "It was fun!",
            category = CategoryModel(1, "Travel"),
            image = "path/to/image"
        )
        val id = dbHelper.insertNewMemory(memory)

        //create a copy and change title
        val updatedMemory = memory.copy(title = "Updated Thailand 2024", id = id.toInt())
        dbHelper.updateMemory(updatedMemory)
        val retrievedMemory = dbHelper.getOneMemory(updatedMemory.id)

        assertEquals("Updated Thailand 2024", retrievedMemory?.title)
    }

    @Test
    fun insertCategory_shouldInsert_NewCategoryToDatabase() {
        //create a category instance and insert to db
        val category = CategoryModel(id = 1, name= "Travel")
        val id = dbHelper.insertCategory(category.name)

        //retrieve category list and find the one that is tested
        val categoryList = dbHelper.getCategories()
        val retrievedCategory = categoryList.find { categoryModel -> categoryModel.id == id.toInt() }

        //test
        assertEquals("Travel", retrievedCategory?.name)
    }

}