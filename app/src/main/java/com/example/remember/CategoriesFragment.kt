package com.example.remember

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
import com.example.remember.models.CategoryModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoriesFragment : Fragment(R.layout.fragment_second) {

    private lateinit var dbHelper: DatabaseHelper
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

        // Check if the activity is an AppCompatActivity before using the ActionBar. Needed for testing purposes
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.cateories)

        dbHelper = DatabaseHelper(requireContext(), getString(R.string.database_name))
        categories = (dbHelper.getCategories())

        recyclerView = view.findViewById(R.id.category_list_rv)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = CategoryAdapter(categories.toList()) { category ->
            showEditDialog(category, dbHelper)
        }
        recyclerView.adapter = adapter

        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener() {
            showEditDialog(null, dbHelper)
        }
    }

    private fun showEditDialog(category: CategoryModel?, dbHelper: DatabaseHelper) {
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
                    dbHelper.updateCategory(category.id, newCategoryName) }
                    else {
                        dbHelper.insertCategory(newCategoryName)
                    }
                    //refreshing categories here, since existing AlertDialog will not trigger onResume
                    refreshCategories()

                }
                else {
                    Toast.makeText(requireContext(), "Category can't be empty", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()

    }

    private fun refreshCategories() {
        categories = dbHelper.getCategories().toList()

        adapter = CategoryAdapter(categories) {category ->
            showEditDialog(category, dbHelper)
        }
        recyclerView.adapter = adapter
    }
}