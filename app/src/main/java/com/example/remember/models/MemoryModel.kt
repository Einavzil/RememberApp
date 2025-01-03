package com.example.remember.models

data class MemoryModel (
    var id: Int,
    val title: String,
    val description: String,
    val category: CategoryModel,
    val image: String?
)