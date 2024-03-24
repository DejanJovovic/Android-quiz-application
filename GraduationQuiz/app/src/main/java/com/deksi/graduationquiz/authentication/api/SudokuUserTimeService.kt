package com.deksi.graduationquiz.authentication.api

import com.deksi.graduationquiz.authentication.model.SudokuUserTime
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SudokuUserTimeService {
    @POST("sudoku-update-time")
    fun updateTime(@Body sudokuUserTime: SudokuUserTime?): Call<Void?>?
}