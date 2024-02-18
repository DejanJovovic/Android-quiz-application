package com.deksi.graduationquiz.password.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ChangePasswordApiService {
    @POST("change-password")
    fun sendEmailForForgotPassword(@Body request: Map<String, String>): Call<Map<String, Any>>
}