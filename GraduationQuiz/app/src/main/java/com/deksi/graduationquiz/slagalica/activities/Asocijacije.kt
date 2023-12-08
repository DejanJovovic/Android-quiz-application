package com.deksi.graduationquiz.slagalica.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.deksi.graduationquiz.databinding.ActivityAsocijacijeBinding

class Asocijacije : AppCompatActivity() {

    private lateinit var binding: ActivityAsocijacijeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAsocijacijeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

    }


    // needs fixing
    //    private fun getRoundData() {
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://192.168.1.2:8080/api/asosijacije/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val asosijacijeService = retrofit.create(AsosijacijeService::class.java)
//
//        val call = asosijacijeService.getRandomRound()
//
//        call.enqueue(object: Callback<AsosijacijeModel> {
//            override fun onResponse(call: Call<AsosijacijeModel>, response: Response<AsosijacijeModel>) {
//                if(response.isSuccessful){
//                     val asosijacijeResponse = response.body()
//
    // testing
//                    binding.buttonA1.text = asosijacijeResponse?.a1
//                    binding.buttonA2.text = asosijacijeResponse?.a2
//                    binding.buttonA3.text = asosijacijeResponse?.a3
//                    binding.buttonA4.text = asosijacijeResponse?.a4
//                    binding.buttonB1.text = asosijacijeResponse?.b1
//                    binding.buttonB2.text = asosijacijeResponse?.b2
//                    binding.buttonB3.text = asosijacijeResponse?.b3
//                    binding.buttonB4.text = asosijacijeResponse?.b4
//                    binding.buttonC1.text = asosijacijeResponse?.c1
//                    binding.buttonC2.text = asosijacijeResponse?.c2
//                    binding.buttonC3.text = asosijacijeResponse?.c3
//                    binding.buttonC4.text = asosijacijeResponse?.c4
//                    binding.buttonD1.text = asosijacijeResponse?.d1
//                    binding.buttonD2.text = asosijacijeResponse?.d2
//                    binding.buttonD3.text = asosijacijeResponse?.d3
//                    binding.buttonD4.text = asosijacijeResponse?.d4
//
//                }
//            }
//
//            override fun onFailure(call: Call<AsosijacijeModel>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//
//        })
//
//    }

}