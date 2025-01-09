package com.example.remember.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remember.R
import com.example.remember.controllers.RecentMemoriesController
import com.example.remember.models.DatabaseHelper
import com.example.remember.models.MemoryModel
import com.example.remember.models.repositories.MemoryRepositoryImpl
import com.example.remember.views.RecentMemoriesView
import com.example.remember.views.activities.CreateMemoryActivity
import com.example.remember.views.activities.ViewMemoryActivity
import com.example.remember.views.adapters.MemoryAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RecentMemoriesFragment : Fragment(R.layout.fragment_first), RecentMemoriesView {

    private lateinit var adapter: MemoryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var controller: RecentMemoriesController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_first, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.recents)

        //initiate database and get only the last 5 added memories
        val dbHelper = DatabaseHelper(requireContext(), getString(R.string.database_name))
        val repository = MemoryRepositoryImpl(dbHelper)
        controller = RecentMemoriesController(repository,this)

        //initiate recycler view and populate cards with memories data
        recyclerView = view.findViewById(R.id.short_memory_rv)
        recyclerView.layoutManager = LinearLayoutManager(context)

        refreshMemoryList()

        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener() {
            val intent = Intent(requireContext(), CreateMemoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshMemoryList()
    }

    private fun refreshMemoryList() {
        controller.loadRecentMemories()
    }

    override fun populateMemoriesDetails(memories: List<MemoryModel>) {
        adapter = MemoryAdapter(requireContext(), memories) { memory ->
            val intent = Intent(requireContext(), ViewMemoryActivity::class.java).apply {
                putExtra("memoryId", memory.id)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter // Set the updated adapter
    }

}