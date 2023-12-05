package com.deksi.graduationquiz.authentication.api

import com.deksi.graduationquiz.authentication.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SignUpService {

    @POST("signup")
    fun signUp(@Body user: User): Call<User>

}