package com.example.remember.models.repositories

import android.net.Uri
import com.example.remember.models.CategoryModel
import com.example.remember.models.DatabaseHelper
import com.example.remember.models.FileHandler
import com.example.remember.models.MemoryModel

class MemoryRepositoryImpl(private val dbHelper: DatabaseHelper, private val fileHandler: FileHandler? = null): IMemoryRepository {
    override fun getCategories(): List<CategoryModel> = dbHelper.getCategories()
    override fun getMemoryById(id: Int): MemoryModel? = dbHelper.getOneMemory(id)
    override fun insertCategory(categoryName: String): Long {
        return dbHelper.insertCategory(categoryName)
    }

    override fun updateCategory(category: CategoryModel) {
        dbHelper.updateCategory(category.id,category.name)
    }

    override fun saveMemory(memory: MemoryModel): Long = dbHelper.insertNewMemory(memory)
    override fun updateMemory(memory: MemoryModel): Boolean {
        dbHelper.updateMemory(memory)
        return true
    }
    override fun saveImage(uri: Uri?): String? {
        return fileHandler?.saveImageToInternalStorage(uri)
    }

    override fun deleteMemory(id: Int): Boolean {
        return dbHelper.deleteMemory(id)
    }

    override fun getMemories(): List<MemoryModel> {
        return dbHelper.getMemories(true)
    }

    override fun getRecentMemories(): List<MemoryModel> {
        return dbHelper.getMemories(false)
    }
}