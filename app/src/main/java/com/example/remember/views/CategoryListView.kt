package com.example.remember.views

import com.example.remember.models.CategoryModel

// Given the simplicity of the app, we can use the same View for every activity/fragment.
// Ideally, each one would have it's own View
interface CategoryListView {
    fun updateCategories(categories: List<CategoryModel>)
}