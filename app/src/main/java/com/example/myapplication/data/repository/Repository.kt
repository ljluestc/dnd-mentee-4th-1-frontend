package com.example.myapplication.data.repository

import com.example.myapplication.data.datasource.remote.api.RecipeDTO

interface Repository {
    fun getAllTimelineList(
        success: (RecipeDTO.PostItems) -> Unit,
        fail: (Throwable) -> Unit
    )
    fun postTimeline(
        postInfo: ArrayList<RecipeDTO.PostItem>,
        success: (RecipeDTO.TimelineResponse) -> Unit,
        fail: (Throwable) -> Unit
    )

    fun saveSearch(recipe : String)

    fun getSavedSearchList() : List<String>
}