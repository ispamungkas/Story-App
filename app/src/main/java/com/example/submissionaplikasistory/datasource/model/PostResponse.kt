package com.example.submissionaplikasistory.datasource.model

import com.google.gson.annotations.SerializedName

data class PostResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
