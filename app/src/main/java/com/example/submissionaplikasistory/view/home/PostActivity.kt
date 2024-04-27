package com.example.submissionaplikasistory.view.home

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.submissionaplikasistory.R
import com.example.submissionaplikasistory.databinding.ActivityPostBinding
import com.example.submissionaplikasistory.databinding.DialogCustomResponseBinding
import com.example.submissionaplikasistory.di.Injection
import com.example.submissionaplikasistory.utils.Resources
import com.example.submissionaplikasistory.utils.Utils
import com.example.submissionaplikasistory.utils.Utils.Companion.reduceImage
import com.example.submissionaplikasistory.utils.dataStore
import com.example.submissionaplikasistory.view.viewmodel.StoryViewModel
import com.example.submissionaplikasistory.view.viewmodel.UserViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var bindingDialog: DialogCustomResponseBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val storyViewModel: StoryViewModel by viewModels {
        Injection.getStoryRepositoryInstance(applicationContext)
    }
    private val userViewModel: UserViewModel by viewModels {
        Injection.getUserRepositoryInstance(application.dataStore)
    }
    private var currentImage: Uri? = null
    private var currentLat: Double? = null
    private var currentLng: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        bindingDialog = DialogCustomResponseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dialog = Utils.dialogInstance(this)

        if (!checkPermission(Manifest.permission.CAMERA)) {
            requestPermission.launch(Manifest.permission.CAMERA)
        }

        supportActionBar?.title = resources.getString(R.string.add_new_story)

        storyViewModel.postResult.observe(this) {
            when (it) {
                is Resources.Loading -> {
                    binding.loading.visibility = View.VISIBLE
                }

                is Resources.OnFailure -> {
                    binding.loading.visibility = View.GONE
                    dialog.show()
                    dialog.setContentView(bindingDialog.root)
                    showSnackBar(it.message, dialog, false, bindingDialog)
                }

                is Resources.OnSuccess -> {
                    binding.loading.visibility = View.GONE
                    dialog.show()
                    dialog.setContentView(bindingDialog.root)
                    it.data.message?.let { it1 -> showSnackBar(it1, dialog, true, bindingDialog) }
                }
            }
        }

        binding.apply {
            btnCamera.setOnClickListener { openCamera() }
            btnGallery.setOnClickListener { openGallery() }
            userViewModel.getUserSession().observe(this@PostActivity) { value ->
                buttonAdd.setOnClickListener {
                    uploadImage(value.token)
                }
            }

            checkLocation.setOnCheckedChangeListener { _, isChecked ->
                checkLocationPermission()
                if (isChecked) {
                    println("lat $currentLat long: $currentLng")
                } else {
                    currentLat = null
                    currentLng = null
                    println("lat $currentLat long: $currentLng")

                }
            }
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this@PostActivity)

    }

    private fun checkLocationPermission() {
        if (
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    currentLat = it.latitude
                    currentLng = it.longitude
                } else {
                    Toast.makeText(this, getString(R.string.enable_gps), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLocation.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLocation = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                checkLocationPermission()
            }

            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                checkLocationPermission()
            }

            else -> {
                Toast.makeText(
                    this@PostActivity,
                    resources.getString(R.string.denied),
                    Toast.LENGTH_SHORT
                ).show()
                binding.checkLocation.isChecked = false
            }
        }
    }

    private fun showSnackBar(
        messages: String,
        dialog: Dialog,
        isSuccess: Boolean,
        bindingDialog: DialogCustomResponseBinding
    ) {
        dialog.setCancelable(false)
        if (isSuccess) {
            bindingDialog.apply {
                message.text = messages
                imageStatus.setImageResource(R.drawable.icon_check)
                actionButton.text = ContextCompat.getString(this@PostActivity, R.string.show_post)
                actionButton.setOnClickListener {
                    dialog.dismiss()
                    this@PostActivity.finish()
                }
            }
        } else {
            bindingDialog.apply {
                imageStatus.setImageResource(R.drawable.icon_close)
                message.text = messages
                actionButton.text = ContextCompat.getString(this@PostActivity, R.string.back)
                actionButton.setOnClickListener { dialog.dismiss() }
            }
        }
    }

    private fun uploadImage(token: String?) {
        binding.loading.visibility = View.VISIBLE
        if (token != null && currentImage != null) {
            val fileImage = Utils.uriToFile(currentImage!!, this@PostActivity).reduceImage()
            val tokenUser = token
            val description = binding.edAddDescription.text?.toString()?.trim()

            val requestBodyDescription = description?.toRequestBody("text/plain".toMediaType())
            val requestImageBody = fileImage.asRequestBody("image/*".toMediaType())
            val multipartImage = MultipartBody.Part.createFormData(
                "photo",
                fileImage.name,
                requestImageBody
            )

            if (requestBodyDescription != null) {
                storyViewModel.postStory(
                    tokenUser,
                    requestBodyDescription,
                    multipartImage,
                    if (currentLng != null) currentLat?.toFloat() else null,
                    if (currentLng != null) currentLat?.toFloat() else null
                )
            }
        } else {
            Toast.makeText(
                this@PostActivity,
                resources.getString(R.string.warning),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openCamera() {

        currentImage = Utils.getImageUri(this@PostActivity)
        requestOpenCamera.launch(currentImage)
    }

    private fun checkPermission(permission: String) = ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(
                this@PostActivity,
                resources.getString(R.string.granted),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this@PostActivity,
                resources.getString(R.string.denied),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val requestOpenCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        if (it) {
            showImage()
        }
    }

    private fun openGallery() {
        requestOpenGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val requestOpenGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {
        if (it != null) {
            currentImage = it
            showImage()
        }
    }

    private fun showImage() {
        currentImage?.let {
            binding.ivImagePost.setImageURI(it)
        }
    }

}