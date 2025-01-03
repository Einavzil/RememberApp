package com.example.remember.models

data class CategoryModel (
    val id: Int,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}