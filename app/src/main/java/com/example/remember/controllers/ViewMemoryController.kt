package com.example.remember.controllers

import com.example.remember.models.repositories.IMemoryRepository
import com.example.remember.views.SingleMemoryView


class ViewMemoryController(
    private val repository: IMemoryRepository,
    private val view: SingleMemoryView
) {
    fun loadMemoryDetails(memoryId: Int) {
        val memory = repository.getMemoryById(memoryId)
        if (memory != null) {
            view.populateMemoryDetails(memory)
        } else {
            view.showError("Memory not found!")
        }
    }

    fun deleteMemory(memoryId: Int): Boolean {
        return repository.deleteMemory(memoryId)
    }
}