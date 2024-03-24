package com.deksi.graduationquiz.authentication.api

import com.deksi.graduationquiz.authentication.model.SudokuUserTime
import retrofit2.Call
import retrofit2.http.GET

interface GetSudokuUserTimeService {
    @GET("sudoku-user-time")
    fun getSudokuUserTime(): Call<List<SudokuUserTime>>
}