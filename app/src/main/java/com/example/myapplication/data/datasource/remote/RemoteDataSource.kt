package com.example.myapplication.data.datasource.remote

import android.content.ContentValues.TAG
import android.util.Log
import com.example.myapplication.data.datasource.remote.api.RecipeApi
import com.example.myapplication.data.datasource.remote.api.RecipeDTO
import retrofit2.Call
import retrofit2.Response

class RemoteDataSource {

    private val recipeApi = RecipeApi.create()

    fun getAllTimelinesFromRemote(
        success: (RecipeDTO.PostItems) -> Unit,
        fail: (Throwable) -> Unit
    ) {
        val callGetAllTimelines = recipeApi.getAllTimelines()
        callGetAllTimelines.enqueue(object : retrofit2.Callback<RecipeDTO.PostItems>{
            override fun onResponse(
                call: Call<RecipeDTO.PostItems>,
                response: Response<RecipeDTO.PostItems>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        success(it)
                        val result : RecipeDTO.PostItems? = response.body()
                        Log.d(TAG, "성공 : ${response.raw()}")
                        Log.d("result", result?.get(0)?.title.toString())
                    }
                }
            }
            override fun onFailure(call: Call<RecipeDTO.PostItems>, t: Throwable) {
                Log.e("/posts", "AlltimelinesFromRemote 가져오기 실패 : " + t)
            }
        })

    }

    fun getRandomRecipes(
        success: (RecipeDTO.APIresponse) -> Unit,
        fail: (Throwable) -> Unit
    ) {
        val callGetRandomRecipes = recipeApi.getRandomRecipes("search","수배")
        callGetRandomRecipes.enqueue(object : retrofit2.Callback<RecipeDTO.APIresponse>{
            override fun onResponse(
                call: Call<RecipeDTO.APIresponse>,
                response: Response<RecipeDTO.APIresponse>
            ) {
                response.body()?.let{
                    success(it)
                }
            }
            override fun onFailure(call: Call<RecipeDTO.APIresponse>, t: Throwable) {
                Log.e("/posts", "RandomRecipes 가져오기 실패 : " + t)
                fail(t)
            }
        })
    }

    /** 홈에서 사용하는 api, queryType = viewTop ,labelTop **/
    fun getHomeRecipes(
        success: (RecipeDTO.RecipeFinal) -> Unit,
        fail: (Throwable) -> Unit
    ) {
        val callHomeRecipes = recipeApi.getHomeRecipes("viewTop")
        callHomeRecipes.enqueue(object : retrofit2.Callback<RecipeDTO.RecipeFinal>{
            override fun onResponse(
                call: Call<RecipeDTO.RecipeFinal>,
                response: Response<RecipeDTO.RecipeFinal>
            ) {
                response.body()?.let{
                    success(it)
                }
            }
            override fun onFailure(call: Call<RecipeDTO.RecipeFinal>, t: Throwable) {
                Log.e("queryType=viewTop", "이번달 top3 item 가져오기 실패 : " + t)
            }
        })
    }

//    fun postTimeline(
//        postInfo: ArrayList<RecipeDTO.PostItem>,
//        success: (RecipeDTO.TimelineResponse) -> Unit,
//        fail: (Throwable) -> Unit
//    ) {
//
//    }
}