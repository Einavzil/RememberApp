package com.example.remember.controllers

import com.example.remember.models.repositories.IMemoryRepository
import com.example.remember.views.MemoryListView


class MemoryListController(
    private val repository: IMemoryRepository,
    private val view: MemoryListView
) {
    fun loadMemories() {
        val memories = repository.getMemories()
        view.populateMemoriesDetails(memories)
    }
}