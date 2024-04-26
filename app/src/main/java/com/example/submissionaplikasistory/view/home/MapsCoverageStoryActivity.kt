package com.example.submissionaplikasistory.view.home

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.submissionaplikasistory.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.submissionaplikasistory.databinding.ActivityMapsCoverageStoryBinding
import com.example.submissionaplikasistory.datasource.local.EntityDaoStory
import com.example.submissionaplikasistory.datasource.model.ListStoryItem
import com.example.submissionaplikasistory.di.Injection
import com.example.submissionaplikasistory.utils.Resources
import com.example.submissionaplikasistory.utils.dataStore
import com.example.submissionaplikasistory.view.viewmodel.StoryViewModel
import com.example.submissionaplikasistory.view.viewmodel.UserViewModel
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapsCoverageStoryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsCoverageStoryBinding
    private val boundsBuilder = LatLngBounds.Builder()
    private var dataStory: List<EntityDaoStory>? = null
    private val storyViewModel : StoryViewModel by viewModels {
        Injection.getStoryRepositoryInstance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsCoverageStoryBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (intent.hasExtra(MainActivity.INTENT_MAPS)){
            fetchDataStory()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.apply {
            isMapToolbarEnabled = true
            isZoomControlsEnabled = true
            isCompassEnabled = true
        }

        dataStory?.let {
            markLocation(it)
        }

        setMapStyle()

    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style can't implement.")
            }
        } catch (exception: Exception) {
            Log.e(TAG, "Error load message: ", exception)
        }
    }

    private fun fetchDataStory() {
        val getIntent = if (Build.VERSION.SDK_INT >= 33) intent.getParcelableArrayListExtra(MainActivity.INTENT_MAPS, EntityDaoStory::class.java)
            else intent.getParcelableExtra(MainActivity.INTENT_MAPS)
        dataStory = getIntent
    }

    private fun markLocation(data: List<EntityDaoStory>) {
        data.forEach {
            val latLong = LatLng(it.lat!!, it.lon!!)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLong)
                    .title(it.name)
                    .snippet(it.description)

            )
            boundsBuilder.include(latLong)
        }

        val bounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds, resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels, 300
            )
        )
    }

    companion object {
        private const val TAG = "Map Activity"
    }

}



