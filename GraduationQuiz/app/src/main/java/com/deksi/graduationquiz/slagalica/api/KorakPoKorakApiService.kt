package com.deksi.graduationquiz.slagalica.api

import com.deksi.graduationquiz.slagalica.model.KorakPoKorakModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface KorakPoKorakApiService {

    @GET("random-round")
    fun getRandomRound(@Query("language") language: String): Call<KorakPoKorakModel>

}