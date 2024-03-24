package com.deksi.graduationquiz.authentication.api

import com.deksi.graduationquiz.authentication.model.UserScore
import retrofit2.Call
import retrofit2.http.GET

interface GetUserScoreService {
    @GET("user-scores")
    fun getUserScores(): Call<List<UserScore>>
}