package com.example.connectmeapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val profileFragment = ProfileFragment()
    private val contactsFragment = ContactsFragment()
    private var activeFragment: Fragment = homeFragment // Keeps track of the current active fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.itemActiveIndicatorColor = null
        bottomNavigation.setItemIconTintList(null)


        // ✅ Initialize Fragments and Load HomeFragment by Default
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, homeFragment, "HOME")
                .add(R.id.fragment_container, searchFragment, "SEARCH").hide(searchFragment)
                .add(R.id.fragment_container, profileFragment, "PROFILE").hide(profileFragment)
                .add(R.id.fragment_container, contactsFragment, "CONTACTS").hide(contactsFragment)
                .commit()
        }

        // ✅ Handle Bottom Navigation Clicks
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> switchFragment(homeFragment, R.id.nav_home)
                R.id.nav_search -> switchFragment(searchFragment, R.id.nav_search)
                R.id.nav_add -> {
                    startActivity(Intent(this, AddPostActivity::class.java))
                    return@setOnItemSelectedListener false // Prevent highlight effect
                }
                R.id.nav_profile -> switchFragment(profileFragment, R.id.nav_profile)
                R.id.nav_contacts -> switchFragment(contactsFragment, R.id.nav_contacts)
            }
            true
        }

    }

    // ✅ Switches to a new fragment only if it's different from the current one
    private fun switchFragment(newFragment: Fragment, selectedId: Int) {
        if (newFragment != activeFragment) { // Prevents unnecessary reloads
            supportFragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(newFragment)
                .commit()
            activeFragment = newFragment // Updates the currently active fragment
        }
        updateIcons(selectedId)
    }

    // ✅ Updates the icons dynamically when selecting a new tab
    private fun updateIcons(selectedId: Int) {
        bottomNavigation.menu.findItem(R.id.nav_home).setIcon(
            if (selectedId == R.id.nav_home) R.drawable.ic_home_active else R.drawable.ic_home_inactive
        )
        bottomNavigation.menu.findItem(R.id.nav_search).setIcon(
            if (selectedId == R.id.nav_search) R.drawable.ic_search_active else R.drawable.ic_search_inactive
        )
        bottomNavigation.menu.findItem(R.id.nav_profile).setIcon(
            if (selectedId == R.id.nav_profile) R.drawable.ic_profile_active else R.drawable.ic_profile_inactive
        )
        bottomNavigation.menu.findItem(R.id.nav_contacts).setIcon(
            if (selectedId == R.id.nav_contacts) R.drawable.ic_contacts_active else R.drawable.ic_contacts_inactive
        )
    }
    private fun resizeSpecificIcons() {
        bottomNavigation.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onGlobalLayout() {
                bottomNavigation.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val displayMetrics = resources.displayMetrics
                val largeSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44f, displayMetrics).toInt()

                val iconMap = mapOf(
                    R.id.nav_home to R.drawable.ic_home_active,
                    R.id.nav_profile to R.drawable.ic_profile_active,
                    R.id.nav_contacts to R.drawable.ic_contacts_active
                )

                for (i in 0 until bottomNavigation.menu.size()) {
                    val menuItem = bottomNavigation.menu.getItem(i)
                    val iconDrawable = resources.getDrawable((iconMap[menuItem.itemId] ?: menuItem.icon) as Int, theme)

                    // ✅ Set icon size dynamically
                    iconDrawable.setBounds(0, 0, largeSize, largeSize)
                    menuItem.icon = iconDrawable
                }
            }
        })
    }



}





//class MainActivity : AppCompatActivity() {
//
//    private lateinit var storiesRecyclerView: RecyclerView
//    private lateinit var storyAdapter: StoryAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//
//        val mainView = findViewById<View>(R.id.main)
//        mainView?.let {
//            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
//                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//                insets
//            }
//        }
//
//
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, HomeFragment()) // This container must be in `activity_main.xml`
//                .commit()
//        }
//
//
//        storiesRecyclerView = findViewById(R.id.stories_recycler_view)
//        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        storiesRecyclerView.layoutManager = layoutManager
//
//
//        storiesRecyclerView.setHasFixedSize(true)  // Helps RecyclerView work faster
//        storiesRecyclerView.isNestedScrollingEnabled = false  // Prevents slow scrolling
//
//        val storyList = mutableListOf(
//            StoryModel(R.drawable.profile_placeholder, isMyStory = true)
//        )
//
//        storyList.addAll(List(14) { StoryModel(R.drawable.profile_placeholder) })
//
//
//        storyAdapter = StoryAdapter(storyList)
//        storiesRecyclerView.adapter = storyAdapter
//
////        val storyList = mutableListOf(
////            StoryModel(R.drawable.user1, isMyStory = true)
////        )
////
////        val realStories = listOf(
////            StoryModel(R.drawable.user2),
////            StoryModel(R.drawable.user3),
////            StoryModel(R.drawable.user4),
////            StoryModel(R.drawable.user5)
////        )
////
////        storyList.addAll(realStories)
////
////        val additionalFakeStories = List(9) { StoryModel(R.drawable.profile_placeholder) }
////        storyList.addAll(additionalFakeStories)
////
////        storyAdapter = StoryAdapter(storyList)
////        storiesRecyclerView.adapter = storyAdapter
//
//
//    }
//}


