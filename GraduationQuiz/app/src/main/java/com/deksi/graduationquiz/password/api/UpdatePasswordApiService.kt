package com.deksi.graduationquiz.password.api

import retrofit2.Call
import retrofit2.http.PUT
import retrofit2.http.Query

interface UpdatePasswordApiService {
    @PUT("update-password")
    fun updatePassword(
        @Query("email") email: String,
        @Query("newPassword") newPassword: String,
        @Query("verificationCode") verificationCode: String
    ): Call<Void>
}