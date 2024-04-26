package com.example.submissionaplikasistory.view.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionaplikasistory.R
import com.example.submissionaplikasistory.databinding.ActivityMainBinding
import com.example.submissionaplikasistory.datasource.local.EntityDaoStory
import com.example.submissionaplikasistory.datasource.model.ListStoryItem
import com.example.submissionaplikasistory.di.Injection
import com.example.submissionaplikasistory.utils.Resources
import com.example.submissionaplikasistory.utils.Utils
import com.example.submissionaplikasistory.utils.dataStore
import com.example.submissionaplikasistory.view.LoginActivity
import com.example.submissionaplikasistory.view.adapter.LoadingStateAdapter
import com.example.submissionaplikasistory.view.adapter.StoryAdapter
import com.example.submissionaplikasistory.view.adapter.StoryAdapter2
import com.example.submissionaplikasistory.view.viewmodel.StoryViewModel
import com.example.submissionaplikasistory.view.viewmodel.UserViewModel
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapterRc: StoryAdapter
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
                goToLoginPage()
            }
        }

        binding.apply {
            rvList.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
            floatingAddPost.setOnClickListener { goToAddPostPage() }
        }

        setUpRecyclerView()

    }

    private fun goToLoginPage() {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToAddPostPage() {
        val intent = Intent(this@MainActivity, PostActivity::class.java)
        startActivity(intent)
    }

    private fun setUpRecyclerView() {
        adapterRc = StoryAdapter { goToDetailPost(it) }

        binding.rvList.adapter = adapterRc.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapterRc.retry()
            }
        )

        userViewModel.getUserSession().observe(this) {
            println(it.token!!)
            storyViewModel.getStoryDao(Utils.getHeader(it.token)).observe(this) { result ->
                adapterRc.submitData(lifecycle, result)
                binding.loading.visibility = View.GONE
            }
        }

        userViewModel.getUserSession()
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

    private fun actionMap() {
        val intent = Intent(this, MapsCoverageStoryActivity::class.java)
        storyViewModel.getAllStoryFromDatabase.observe(this) {
            intent.putParcelableArrayListExtra(INTENT_MAPS, ArrayList(it))
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
            R.id.action_map -> ( actionMap() )
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val INTENT_MAPS = "intent_maps"
    }
}