package com.deksi.graduationquiz.slagalica.api

import com.deksi.graduationquiz.slagalica.model.KoZnaZnaModel
import retrofit2.Call
import retrofit2.http.GET

interface KoZnaZnaApiService {

    @GET("random-rounds")
    fun getRandomRounds(): Call<List<KoZnaZnaModel>>

}