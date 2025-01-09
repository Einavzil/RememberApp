package com.example.remember.models.repositories

import android.net.Uri
import com.example.remember.models.CategoryModel
import com.example.remember.models.MemoryModel

interface IMemoryRepository {
    fun getCategories(): List<CategoryModel>
    fun getMemoryById(id: Int): MemoryModel?
    fun insertCategory(categoryName: String): Long
    fun updateCategory(category: CategoryModel)
    fun saveMemory(memory: MemoryModel): Long
    fun updateMemory(memory: MemoryModel): Boolean
    fun saveImage(uri: Uri?): String?
    fun deleteMemory(id: Int): Boolean
    fun getMemories(): List<MemoryModel>
    fun getRecentMemories(): List<MemoryModel>
}