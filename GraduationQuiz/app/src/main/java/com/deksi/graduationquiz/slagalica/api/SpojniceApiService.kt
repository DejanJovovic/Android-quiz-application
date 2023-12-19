package com.deksi.graduationquiz.slagalica.api

import com.deksi.graduationquiz.slagalica.model.SpojniceModel
import retrofit2.Call
import retrofit2.http.GET

interface SpojniceApiService {

    @GET("random-round")
    fun getRandomRound(): Call<SpojniceModel>

}