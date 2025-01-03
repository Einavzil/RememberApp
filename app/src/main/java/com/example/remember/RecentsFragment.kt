package com.example.remember

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remember.models.MemoryModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RecentsFragment : Fragment(R.layout.fragment_first) {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recentMemoryList: List<MemoryModel>
    private lateinit var adapter: MemoryAdapter
    private lateinit var recyclerView: RecyclerView

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
        dbHelper = DatabaseHelper(requireContext(), getString(R.string.database_name))
        recentMemoryList = (dbHelper.getMemories(false))

        //initiate recycler view and populate cards with memories data
        recyclerView = view.findViewById(R.id.short_memory_rv)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = MemoryAdapter(requireContext(), recentMemoryList) {_ ->
            val intent = Intent(context, ViewMemoryActivity::class.java)
            startActivity(intent)
        }

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
        recentMemoryList = dbHelper.getMemories(false) // Get updated list from the database
        adapter = MemoryAdapter(requireContext(), recentMemoryList) { memory ->
            val intent = Intent(requireContext(), ViewMemoryActivity::class.java).apply {
                putExtra("memoryId", memory.id)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter // Set the updated adapter
    }

}