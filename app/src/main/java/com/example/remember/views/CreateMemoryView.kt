package com.example.remember.views

import com.example.remember.models.CategoryModel
import com.example.remember.models.MemoryModel

// Given the simplicity of the app, we can use the same View for every activity/fragment.
// Ideally, each one would have it's own View
interface CreateMemoryView {
    fun updateCategories(categories: List<CategoryModel>)
    fun populateMemoryDetails(memory: MemoryModel)
    fun showMessage(message: String)
    fun showError(message: String)
    fun closeView()
}