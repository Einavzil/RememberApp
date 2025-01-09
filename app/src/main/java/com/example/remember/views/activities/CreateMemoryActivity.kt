package com.example.remember.views.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.remember.R
import com.example.remember.controllers.CreateMemoryController
import com.example.remember.models.CategoryModel
import com.example.remember.models.DatabaseHelper
import com.example.remember.models.FileHandler
import com.example.remember.models.MemoryModel
import com.example.remember.models.repositories.MemoryRepositoryImpl
import com.example.remember.views.CreateMemoryView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
class CreateMemoryActivity : AppCompatActivity(), CreateMemoryView {

    private lateinit var controller: CreateMemoryController
    private lateinit var titleInput: TextInputEditText
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var categoryDropDown: MaterialAutoCompleteTextView
    private lateinit var imageView: ImageView
    private var memoryId: Int = -1
    private var imagePath: String? = null
    private lateinit var categories: List<CategoryModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_memory)

        // Initialize UI components
        titleInput = findViewById(R.id.title_input)
        descriptionInput = findViewById(R.id.description_input)
        categoryDropDown = findViewById(R.id.category_dropdown)
        imageView = findViewById(R.id.selected_image)

        // Initialize repository and controller
        val repository = MemoryRepositoryImpl(
            DatabaseHelper(this, getString(R.string.database_name)),
            FileHandler(this)
        )
        controller = CreateMemoryController(repository, this)

        // Retrieve memory ID from intent
        memoryId = intent.getIntExtra("memoryId", -1)

        // Load data into the view
        controller.loadCategories()
        if (memoryId != -1) {
            controller.loadMemoryDetails(memoryId)
        }

        // Set up save button click listener
        findViewById<Button>(R.id.save_button).setOnClickListener {
            saveMemory()
        }
        var topAppBar: MaterialToolbar = findViewById(R.id.topAppBar)
        topAppBar.title = getString(R.string.create_memory_title)

        //exit button click listener
        topAppBar.setNavigationOnClickListener {
            finish()
        }
        // Set up image picker button
        findViewById<Button>(R.id.upload_image_button).setOnClickListener {
            pickImage()
        }
    }

    private fun saveMemory() {
        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        val selectedCategoryName = categoryDropDown.text.toString().trim()
        val selectedCategory = categories.find { it.name == selectedCategoryName }
        //several checks to ensure fields are not empty
        if (titleInput.text.toString().trim().isEmpty()) {
            titleInput.error = getString(R.string.title_empty)
            return
        }
        if (descriptionInput.text.toString().trim().isEmpty()) {
            descriptionInput.error = getString(R.string.description_empty)
            return
        }
        if (categoryDropDown.text.toString().trim().isEmpty()) {
            categoryDropDown.error = getString(R.string.category_not_selected)
            return
        }
        if (selectedCategory == null) {
            showError("Please select a valid category!")
            return
        }


        val memory = MemoryModel(
            id = memoryId,
            title = title,
            description = description,
            category = selectedCategory,
            image = imagePath
        )
        controller.saveMemory(memory)
    }

    override fun updateCategories(categories: List<CategoryModel>) {
        this.categories = categories
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories.map { it.name })
        categoryDropDown.setAdapter(adapter)
    }

    override fun populateMemoryDetails(memory: MemoryModel) {
        titleInput.setText(memory.title)
        descriptionInput.setText(memory.description)
        categoryDropDown.setText(memory.category.name, false)
        imagePath = memory.image

        memory.image?.let {
            val bitmap = BitmapFactory.decodeFile(it)
            imageView.setImageBitmap(bitmap)
        }
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun closeView() {
        finish()
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            val imageUri = data?.data
            imageView.setImageURI(imageUri)
            imagePath = controller.saveImage(imageUri)
        }
    }
    companion object {
        private const val IMAGE_PICK_CODE = 12
    }
}