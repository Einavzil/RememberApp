package com.example.remember.controllers

import com.example.remember.models.CategoryModel
import com.example.remember.models.repositories.IMemoryRepository
import com.example.remember.views.CategoryListView


class CategoryListController(
    private val repository: IMemoryRepository,
    private val view: CategoryListView
) {
    fun loadCategories() {
        val categories = repository.getCategories()
        view.updateCategories(categories)
    }

    fun insertCategory(categoryName: String): Long {
        return repository.insertCategory(categoryName)
    }

    fun updateCategory(categoryId: Int, categoryName: String) {
        return repository.updateCategory(CategoryModel(categoryId,categoryName))
    }

}