package com.deksi.graduationquiz.authentication.api

import com.deksi.graduationquiz.authentication.model.UserScore
import retrofit2.Call
import retrofit2.http.Body

import retrofit2.http.POST

interface UserScoreService {
    @POST("update-score")
    fun updateScore(@Body userScore: UserScore?): Call<Void?>?
}