package com.example.remember.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.ui.AppBarConfiguration
import com.example.remember.R
import com.example.remember.views.fragments.CategoryListFragment
import com.example.remember.views.fragments.MemoryListFragment
import com.example.remember.views.fragments.RecentMemoriesFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.navigation)

        //Setting the default view when opening the app
        if (savedInstanceState == null) {
            replaceFragment(RecentMemoriesFragment())
        }

        //setting up a button listener for each button on the navigation bar
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragment1 -> {
                    replaceFragment(RecentMemoriesFragment())
                    true
                }

                R.id.fragment2 -> {
                    replaceFragment(CategoryListFragment())
                    true
                }

                R.id.fragment3 -> {
                    replaceFragment(MemoryListFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
