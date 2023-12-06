package com.deksi.graduationquiz.authentication.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {


    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

}