package com.scottparrillo.gamepulse

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class FriendsActivity : AppCompatActivity() {

    private lateinit var searchBar: EditText
    private lateinit var addFriendButton: Button
    private lateinit var friendsListView: ListView
    private val friendsList = mutableListOf<String>()
    private val displayedFriendsList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        // Initialize views
        searchBar = findViewById(R.id.search_bar)
        addFriendButton = findViewById(R.id.add_friend_button)
        friendsListView = findViewById(R.id.friends_list)

        // Setup toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set the toolbar title color to black
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black))

        // Ensure the back button is black
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.black))

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()  // Handle back press using OnBackPressedDispatcher
        }

        // Setup friends list adapter
        val friendsAdapter = FriendsAdapter()
        friendsListView.adapter = friendsAdapter

        // Load friends list from file
        loadFriendsList()

        // Add friend button click listener
        addFriendButton.setOnClickListener {
            showAddFriendDialog()
        }

        // Search bar listener
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterFriends(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed
            }
        })
    }

    private fun showAddFriendDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Friend")

        // Set up the input
        val input = EditText(this)
        input.hint = "Enter friend's name"
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("Add") { dialog, _ ->
            val friendName = input.text.toString()
            if (friendName.isNotBlank()) {
                addFriend(friendName)
                input.text.clear()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun addFriend(friendName: String) {
        if (friendName !in friendsList) {
            friendsList.add(friendName)
            displayedFriendsList.add(friendName)
            (friendsListView.adapter as FriendsAdapter).notifyDataSetChanged()
            saveFriendsList()  // Save to file after adding
            Toast.makeText(this, "Friend added", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Friend already in the list", Toast.LENGTH_SHORT).show()
        }
    }

    private fun filterFriends(query: String) {
        val filteredList = friendsList.filter { it.contains(query, ignoreCase = true) }
        displayedFriendsList.clear()
        displayedFriendsList.addAll(filteredList)
        (friendsListView.adapter as FriendsAdapter).notifyDataSetChanged()
    }

    private fun saveFriendsList(): Boolean {
        return try {
            val fos = openFileOutput("friends_list", MODE_PRIVATE)
            val oos = ObjectOutputStream(fos)
            oos.writeObject(friendsList)
            oos.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun loadFriendsList() {
        try {
            val fis = openFileInput("friends_list")
            val ois = ObjectInputStream(fis)
            @Suppress("UNCHECKED_CAST")
            val loadedList = ois.readObject() as? MutableList<String>
            ois.close()
            if (loadedList != null) {
                friendsList.clear()
                friendsList.addAll(loadedList)
                displayedFriendsList.clear()
                displayedFriendsList.addAll(friendsList)
                (friendsListView.adapter as FriendsAdapter).notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class FriendsAdapter : ArrayAdapter<String>(this, R.layout.list_item_friend, displayedFriendsList) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_friend, parent, false)
            val friendNameTextView: TextView = view.findViewById(R.id.friend_name)
            val deleteButton: Button = view.findViewById(R.id.delete_button)

            val friendName = getItem(position) ?: return view

            friendNameTextView.text = friendName

            deleteButton.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete $friendName?")
                    .setPositiveButton("Delete") { _, _ ->
                        removeFriend(friendName)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

            return view
        }

        private fun removeFriend(friendName: String) {
            friendsList.remove(friendName)
            displayedFriendsList.remove(friendName)
            notifyDataSetChanged()
            saveFriendsList()  // Save to file after removing
            Toast.makeText(this@FriendsActivity, "Friend removed", Toast.LENGTH_SHORT).show()
        }
    }
}
