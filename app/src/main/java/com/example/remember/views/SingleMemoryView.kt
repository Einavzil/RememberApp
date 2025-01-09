package com.example.remember.views

import com.example.remember.models.MemoryModel

// Given the simplicity of the app, we can use the same View for every activity/fragment.
// Ideally, each one would have it's own View
interface SingleMemoryView {
    fun populateMemoryDetails(memory: MemoryModel)
    fun showError(message: String)
}