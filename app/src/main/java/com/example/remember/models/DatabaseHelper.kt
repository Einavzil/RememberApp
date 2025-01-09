package com.example.remember.models

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

open class DatabaseHelper(context: Context, dbName: String) : SQLiteOpenHelper(context, dbName, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create memory table with all attributes, association with category table
        val createMemoryTable = """
        CREATE TABLE MEMORY_TABLE (
            ID INTEGER PRIMARY KEY AUTOINCREMENT,
            TITLE TEXT,
            DESCRIPTION TEXT DEFAULT NULL,
            CATEGORY_ID INTEGER,
            IMAGE_PATH TEXT DEFAULT NULL,
            DATE DATETIME DEFAULT CURRENT_DATE,
            FOREIGN KEY(CATEGORY_ID) REFERENCES CATEGORY_TABLE(ID)
        )
    """.trimIndent()

        // Create category table with attributes
        val createCategoryTable = """
        CREATE TABLE CATEGORY_TABLE (
            ID INTEGER PRIMARY KEY AUTOINCREMENT,
            NAME TEXT
        )
    """.trimIndent()


        // Execute to create the database
        db.execSQL(createMemoryTable)
        db.execSQL(createCategoryTable)
        var values = ContentValues().apply {
            put("NAME", "Travel")
        }
        db.insert("CATEGORY_TABLE", null, values)

        values = ContentValues().apply {
            put("NAME", "Cats")
        }
        db.insert("CATEGORY_TABLE", null, values)

        values = ContentValues().apply {
            put("NAME", "Recipes")
        }
        db.insert("CATEGORY_TABLE", null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database schema upgrades here
    }

    fun getCategories(): List<CategoryModel> {
        val categories = ArrayList<CategoryModel>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT ID, NAME FROM CATEGORY_TABLE", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("NAME"))
                categories.add(CategoryModel(id,name))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return categories
    }

    //Insert new memory to the db
    fun insertNewMemory(memory: MemoryModel): Long {
        val db = this.writableDatabase
        val statement = ContentValues().apply {
            put("TITLE", memory.title)
            put("DESCRIPTION", memory.description)
            put("CATEGORY_ID", memory.category.id)
            put("IMAGE_PATH", memory.image)
        }
        val id = db.insert("MEMORY_TABLE", null, statement)
        db.close()
        return id
    }

    //Update changed category name to the id in db
    fun updateCategory(categoryId: Int, newCategoryName: String) {
        val db = this.writableDatabase
        val statement = ContentValues().apply {
            put("NAME", newCategoryName)
        }
        db.update("CATEGORY_TABLE", statement, "ID = ?", arrayOf(categoryId.toString()))
        db.close()
    }

    //Insert new category created to the db
    fun insertCategory(categoryName: String) : Long {
        val db = this.writableDatabase
        val statement = ContentValues().apply {
            put("NAME", categoryName)
        }
        val id = db.insert("CATEGORY_TABLE",null, statement)
        db.close()
        return id
    }

    fun getMemories(fullList: Boolean): List<MemoryModel> {
        val recentMemoryList = ArrayList<MemoryModel>()
        val db = this.readableDatabase

        var query = """
            SELECT m.ID, TITLE,DESCRIPTION, m.CATEGORY_ID, IMAGE_PATH, c.NAME as CATEGORY_NAME 
            FROM MEMORY_TABLE m 
            INNER JOIN CATEGORY_TABLE as c 
                ON c.ID = m.CATEGORY_ID
            ORDER BY m.DATE DESC
    """.trimIndent()
        if (!fullList){ query += " LIMIT 5" }

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("TITLE"))
                val description = cursor.getString(cursor.getColumnIndexOrThrow("DESCRIPTION"))
                val categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("CATEGORY_ID"))
                val categoryName = cursor.getString(cursor.getColumnIndexOrThrow("CATEGORY_NAME"))
                val image = cursor.getString(cursor.getColumnIndexOrThrow("IMAGE_PATH"))

                recentMemoryList.add(MemoryModel(
                    id = id,
                    title = title,
                    description = description,
                    CategoryModel(id = categoryId, name = categoryName),
                    image = image))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return recentMemoryList
    }

    fun getOneMemory(memoryId: Int): MemoryModel? {
        var memory: MemoryModel? = null
        val db = this.readableDatabase

        val query = """
            SELECT m.ID, TITLE,DESCRIPTION, m.CATEGORY_ID, IMAGE_PATH, c.NAME as CATEGORY_NAME
            FROM MEMORY_TABLE m
            INNER JOIN CATEGORY_TABLE as c 
                ON c.ID = m.CATEGORY_ID
            WHERE m.ID = ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(memoryId.toString()))
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
            val title = cursor.getString(cursor.getColumnIndexOrThrow("TITLE"))
            val description = cursor.getString(cursor.getColumnIndexOrThrow("DESCRIPTION"))
            val categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("CATEGORY_ID"))
            val categoryName = cursor.getString(cursor.getColumnIndexOrThrow("CATEGORY_NAME"))
            val image = cursor.getString(cursor.getColumnIndexOrThrow("IMAGE_PATH"))

            memory = MemoryModel(
                id = id,
                title = title,
                description = description,
                CategoryModel(id = categoryId, name = categoryName),
                image = image
            )
        }
        cursor.close()
        db.close()
        return memory
    }

    //update an existing memory
    fun updateMemory(memory: MemoryModel) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("TITLE", memory.title)
            put("DESCRIPTION", memory.description)
            put("CATEGORY_ID", memory.category.id)
            put("IMAGE_PATH", memory.image)
        }
        db.update("MEMORY_TABLE", values, "ID=?", arrayOf(memory.id.toString()))
        db.close()
    }

    fun deleteMemory(memoryId: Int): Boolean {
        val db = this.writableDatabase
        val rowsDeleted = db.delete("MEMORY_TABLE", "ID=?", arrayOf(memoryId.toString()))
        db.close()
        return rowsDeleted > 0
    }
}
