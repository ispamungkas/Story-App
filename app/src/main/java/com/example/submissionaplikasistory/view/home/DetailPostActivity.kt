package com.example.submissionaplikasistory.view.home

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.submissionaplikasistory.databinding.ActivityDetailPostBinding
import com.example.submissionaplikasistory.di.Injection
import com.example.submissionaplikasistory.utils.Resources
import com.example.submissionaplikasistory.utils.dataStore
import com.example.submissionaplikasistory.view.viewmodel.StoryViewModel
import com.example.submissionaplikasistory.view.viewmodel.UserViewModel

class DetailPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPostBinding
    private val storyViewModel: StoryViewModel by viewModels {
        Injection.getStoryRepositoryInstance(applicationContext)
    }
    private val userViewModel: UserViewModel by viewModels {
        Injection.getUserRepositoryInstance(application.dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(ID_POST)) {
            userViewModel.getUserSession().observe(this) {
                val id = intent.getStringExtra(ID_POST)
                println(it.token)
                println(id)
                if (it.token != null && id != null) storyViewModel.getDetailStory(it.token, id)
            }

        }

        attachDataToDisplay()
    }

    private fun attachDataToDisplay() {
        storyViewModel.detailStory.observe(this) {
            binding.loading.visibility = View.VISIBLE
            when(it) {
                is Resources.Loading -> { binding.loading.visibility = View.VISIBLE }
                is Resources.OnFailure -> { binding.loading.visibility = View.GONE }
                is Resources.OnSuccess -> {
                    binding.loading.visibility = View.GONE
                    binding.apply {
                        Glide.with(this@DetailPostActivity)
                            .load(it.data.photoUrl)
                            .into(ivDetailPhoto)
                        tvDetailName.text = it.data.name
                        tvDetailDate.text = it.data.createdAt
                        tvDetailDescription.text = it.data.description
                    }
                }
            }
        }
    }

    companion object {
         const val  ID_POST = "ID credential post"
    }
}