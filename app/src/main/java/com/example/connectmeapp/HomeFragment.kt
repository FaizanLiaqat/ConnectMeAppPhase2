package com.example.connectmeapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class HomeFragment : Fragment() {

    private lateinit var homeRecyclerView: RecyclerView
    private lateinit var homeAdapter: HomeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        //  Handle Click on Notification Icon
        val notificationIcon = view.findViewById<ImageView>(R.id.notification_icon)
        notificationIcon.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
        }

        homeRecyclerView = view.findViewById(R.id.home_recycler_view)
        homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // ✅ Sample Stories (No Real Images)
        val storyList = mutableListOf(
            StoryModel(R.drawable.profile_placeholder, isMyStory = true) // My Story
        )

        // ✅ Add Fake Stories (Faster Loading)
        storyList.addAll(
            List(10) { StoryModel(R.drawable.profile_placeholder) } // 10 Placeholder Stories
        )

        // ✅ Sample Posts (No Real Images)
        val posts = listOf(
            PostModel(R.drawable.profile_placeholder, "Alice", R.drawable.profile_placeholder, "Sample caption 1"),
            PostModel(R.drawable.profile_placeholder, "Bob", R.drawable.profile_placeholder, "Sample caption 2"),
            PostModel(R.drawable.profile_placeholder, "Charlie", R.drawable.profile_placeholder, "Sample caption 3")
        )

        // ✅ Combine Stories & Posts in One List
        val homeItems = mutableListOf<HomeItem>()
        homeItems.add(HomeItem.StoryList(storyList)) // Stories at the top
        posts.forEach { homeItems.add(HomeItem.PostItem(it)) } // Posts below

        // ✅ Set Adapter
        homeAdapter = HomeAdapter(homeItems)
        homeRecyclerView.adapter = homeAdapter

        return view
    }
}


//class HomeFragment : Fragment() {
//
//    private lateinit var homeRecyclerView: RecyclerView
//    private lateinit var homeAdapter: HomeAdapter
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.fragment_home, container, false)
//
//        homeRecyclerView = view.findViewById(R.id.home_recycler_view)
//        homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        val storyList = mutableListOf(
//            StoryModel(R.drawable.user1, isMyStory = true)
//        )
//        // ✅ Load Stories
//        val stories = listOf(
//            StoryModel(R.drawable.user1, isMyStory = true),
//            StoryModel(R.drawable.user2),
//            StoryModel(R.drawable.user3),
//            StoryModel(R.drawable.user4)
//        )
//
//        storyList.addAll(stories)
//
//        val additionalFakeStories = List(9) { StoryModel(R.drawable.profile_placeholder) }
//        storyList.addAll(additionalFakeStories)
//
//        // ✅ Load Posts
//        val posts = listOf(
//            PostModel(R.drawable.user1, "Alice", R.drawable.burger_post, "Enjoying the beach!"),
//            PostModel(R.drawable.user2, "Bob", R.drawable.pizza_post, "Amazing sunset view!"),
//            PostModel(R.drawable.user3, "Charlie", R.drawable.construction_post, "Great food today!")
//        )
//
//
//        // ✅ Combine Stories & Posts in One List
//        val homeItems = mutableListOf<HomeItem>()
//        homeItems.add(HomeItem.StoryList(storyList)) // Stories at the top
//        posts.forEach { homeItems.add(HomeItem.PostItem(it)) } // Posts below
//
//        // ✅ Set Adapter
//        homeAdapter = HomeAdapter(homeItems)
//        homeRecyclerView.adapter = homeAdapter
//
//        return view
//    }
//}
//
//
//
//
////PostModel(R.drawable.user1, "Alice", R.drawable.burger_post, "Enjoying the beach!"),
////PostModel(R.drawable.user2, "Bob", R.drawable.pizza_post, "Amazing sunset view!"),
////PostModel(R.drawable.user3, "Charlie", R.drawable.construction_post, "Great food today!"),
////PostModel(R.drawable.user4, "David", R.drawable.elon_musk_post, "Nature is beautiful"),
////PostModel(R.drawable.user5, "Emma", R.drawable.graphic_info_post, "Hiking adventure!")

