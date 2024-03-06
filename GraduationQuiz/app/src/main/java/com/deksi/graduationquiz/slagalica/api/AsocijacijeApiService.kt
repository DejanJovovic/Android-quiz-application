package com.deksi.graduationquiz.slagalica.api

import com.deksi.graduationquiz.slagalica.model.AsocijacijeModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AsocijacijeApiService {

    @GET("random-round")
    fun getRandomRound(@Query("language") language: String): Call<AsocijacijeModel>
}