package com.example.submissionaplikasistory

import com.example.submissionaplikasistory.datasource.local.EntityDaoStory

object DataDummy {
    fun generateEntityResponse(): List<EntityDaoStory> {
        val items: MutableList<EntityDaoStory> = arrayListOf()
        for (i in 0..100) {
            val quote = EntityDaoStory(
                id = "story-JpTtDha5AxTFJT9q",
                name = "Dicoding",
                description = "1. Miliki Motivasi yang Tinggi 2. Bersikap Persisten saat dalam Berlatih dan Menghadapi Tantangan 3. Selalu Percaya Diri dan Mencari Apa Passion Kita",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1687835950650_XvmTvFBv.jpg",
                createdAt = "2023-06-27T03:19:10.651Z",
                lat = -8.8540315,
                lon = 121.6438983
            )
            items.add(quote)
        }
        return items
    }
}