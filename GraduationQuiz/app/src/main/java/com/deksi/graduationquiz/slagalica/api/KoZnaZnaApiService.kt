package com.deksi.graduationquiz.slagalica.api

import com.deksi.graduationquiz.slagalica.model.KoZnaZnaModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface KoZnaZnaApiService {

    @GET("random-rounds")
    fun getRandomRounds(@Query("language") language: String): Call<List<KoZnaZnaModel>>

}