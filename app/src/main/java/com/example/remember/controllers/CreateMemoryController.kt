package com.example.remember.controllers

import android.net.Uri
import com.example.remember.models.MemoryModel
import com.example.remember.models.repositories.IMemoryRepository
import com.example.remember.views.CreateMemoryView


class CreateMemoryController(
    private val repository: IMemoryRepository,
    private val view: CreateMemoryView
) {
    fun loadCategories() {
        val categories = repository.getCategories()
        if (categories.isEmpty()) {
            view.showError("No categories available!")
        } else {
            view.updateCategories(categories)
        }
    }

    fun saveMemory(memory: MemoryModel) {
        if (memory.id == -1) {
            repository.saveMemory(memory)
            view.showMessage("Memory saved!")
        } else {
            repository.updateMemory(memory)
            view.showMessage("Memory updated!")
        }
        view.closeView()
    }

    fun loadMemoryDetails(memoryId: Int) {
        val memory = repository.getMemoryById(memoryId)
        if (memory != null) {
            view.populateMemoryDetails(memory)
        } else {
            view.showError("Memory not found!")
        }
    }

    fun saveImage(uri: Uri?): String? {
        return repository.saveImage(uri)
    }
}