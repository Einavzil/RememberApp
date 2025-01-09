package com.example.remember.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remember.R
import com.example.remember.controllers.CategoryListController
import com.example.remember.models.CategoryModel
import com.example.remember.models.DatabaseHelper
import com.example.remember.models.repositories.MemoryRepositoryImpl
import com.example.remember.views.CategoryListView
import com.example.remember.views.adapters.CategoryAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoryListFragment : Fragment(R.layout.fragment_second), CategoryListView {

    private lateinit var controller: CategoryListController
    private lateinit var categories: List<CategoryModel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_second, container, false)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.cateories)

        val dbHelper = DatabaseHelper(requireContext(), getString(R.string.database_name))
        val repository = MemoryRepositoryImpl(dbHelper)
        controller = CategoryListController(repository, this)

        recyclerView = view.findViewById(R.id.category_list_rv)
        recyclerView.layoutManager = LinearLayoutManager(context)

        refreshCategories()

        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener() {
            showEditDialog(null)
        }
    }

    private fun showEditDialog(category: CategoryModel?) {
        val editText = EditText(requireContext())
        var title = "Create category"
        if (category != null) {
            title = "Edit Category"
            editText.setText(category.name)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("Save") {_, _ ->
                val newCategoryName = editText.text.toString().trim()
                if (newCategoryName.isNotEmpty()) {
                    if (category != null) {
                        controller.updateCategory(category.id, newCategoryName) }
                    else {
                        controller.insertCategory(newCategoryName)
                    }
                    //refreshing categories here, since existing AlertDialog will not trigger onResume
                    refreshCategories()

                }
                else {
                    Toast.makeText(requireContext(), "Category can't be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun refreshCategories() {
        controller.loadCategories()
    }

    override fun updateCategories(categories: List<CategoryModel>) {
        this.categories = categories
        adapter = CategoryAdapter(this.categories) { category ->
            showEditDialog(category)
        }
        recyclerView.adapter = adapter
    }
}