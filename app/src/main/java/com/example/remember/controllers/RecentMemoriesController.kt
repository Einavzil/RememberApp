package com.example.remember.controllers

import com.example.remember.models.repositories.IMemoryRepository
import com.example.remember.views.RecentMemoriesView


class RecentMemoriesController(
    private val repository: IMemoryRepository,
    private val view: RecentMemoriesView
) {
    fun loadRecentMemories() {
        val memories = repository.getRecentMemories()
        view.populateMemoriesDetails(memories)
    }
}