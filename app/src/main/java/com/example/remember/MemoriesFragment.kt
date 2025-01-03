package com.example.remember

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remember.models.MemoryModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MemoriesFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var memoryList: List<MemoryModel>
    private lateinit var adapter: MemoryAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_third, container, false)
        setHasOptionsMenu(true)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.all_memories)

        dbHelper = DatabaseHelper(requireContext(), getString(R.string.database_name))
        memoryList = (dbHelper.getMemories(true))

        recyclerView = view.findViewById(R.id.memory_rv)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = MemoryAdapter(requireContext(), memoryList) {memory ->
            val intent = Intent(requireContext(), ViewMemoryActivity::class.java).apply {
                //pass the memory ID to the intent to fetch more details
                putExtra("memoryId", memory.id)
            }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.third_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_sort) {
            sortMemoriesByCategory()
            return true
        } else {
            return false
        }
    }

    private fun refreshMemoryList() {
        //get updated list from db and re-initializing the adapter
        memoryList = dbHelper.getMemories(true)
        adapter = MemoryAdapter(requireContext(), memoryList) { memory ->
            val intent = Intent(requireContext(), ViewMemoryActivity::class.java).apply {
                putExtra("memoryId", memory.id)
            }
            startActivity(intent)
        }
        //setting the updated adapter
        recyclerView.adapter = adapter
    }

    private fun sortMemoriesByCategory() {
        memoryList = memoryList.sortedBy { it.category.name }
        adapter = MemoryAdapter(requireContext(), memoryList) {memory ->
            val intent = Intent(requireContext(), ViewMemoryActivity::class.java).apply{
                putExtra("memoryId", memory.id)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }
}