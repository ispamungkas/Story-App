package com.example.submissionaplikasistory.view.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionaplikasistory.R
import com.example.submissionaplikasistory.databinding.ActivityMainBinding
import com.example.submissionaplikasistory.datasource.local.EntityDaoStory
import com.example.submissionaplikasistory.datasource.model.ListStoryItem
import com.example.submissionaplikasistory.di.Injection
import com.example.submissionaplikasistory.utils.Resources
import com.example.submissionaplikasistory.utils.dataStore
import com.example.submissionaplikasistory.view.LoginActivity
import com.example.submissionaplikasistory.view.adapter.StoryAdapter
import com.example.submissionaplikasistory.view.viewmodel.StoryViewModel
import com.example.submissionaplikasistory.view.viewmodel.UserViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter
    private val userViewModel: UserViewModel by viewModels {
        Injection.getUserRepositoryInstance(application.dataStore)
    }
    private val storyViewModel: StoryViewModel by viewModels {
        Injection.getStoryRepositoryInstance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val action = supportActionBar
        action?.title = ContextCompat.getString(this, R.string.story)

        // Check user session
        userViewModel.getUserSession().observe(this) {
            if (it.token == null) {
                println(it)
                goToLoginPage()
            }
        }

        callStory()

        storyViewModel.story.observe(this) {
            binding.loading.visibility = View.VISIBLE
            when (it) {
                is Resources.Loading -> {
                    binding.loading.visibility = View.VISIBLE
                }
                is Resources.OnFailure -> {
                    binding.loading.visibility = View.GONE
                    println(it.message)
                }
                is Resources.OnSuccess -> {
                    binding.loading.visibility = View.GONE
                    setUpRecyclerView(it.data)
                    storeDataToLocal(it.data)
                }
            }
        }

        binding.apply {
            rvList.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                setHasFixedSize(true)
            }
            floatingAddPost.setOnClickListener { goToAddPostPage() }
        }


    }

    private fun storeDataToLocal(listStoryItem: List<ListStoryItem?>) {
        listStoryItem.map {
            val entityDaoStory = EntityDaoStory(
                photoUrl = it?.photoUrl!!,
                name = it.name!!,
                desc = it.description!!,
            )
            storyViewModel.storeStory(entityDaoStory)
        }
    }

    private fun goToLoginPage() {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun callStory() {
        userViewModel.getUserSession().observe(this) {
            val token = it.token
            if (token != null) {
                println(token)
                storyViewModel.getAllStory(token)
            }
        }
    }

    private fun goToAddPostPage() {
        val intent = Intent(this@MainActivity, PostActivity::class.java)
        startActivity(intent)
    }

    private fun setUpRecyclerView(listStoryItem: List<ListStoryItem?>) {
        adapter = StoryAdapter { goToDetailPost(it) }
        adapter.setList(listStoryItem)
        binding.rvList.adapter = adapter
    }

    private fun actionLogout() {
        userViewModel.deleteUserSession()
    }

    private fun goToDetailPost(id: String?){
        val intent = Intent(this@MainActivity, DetailPostActivity::class.java)
        id?.let {
            intent.putExtra(DetailPostActivity.ID_POST, it)
        }
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_actionbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> { actionLogout() }
        }
        return super.onOptionsItemSelected(item)
    }
}