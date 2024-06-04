package com.example.fb_checklist.engines

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.example.fb_checklist.ApiInterface
import com.example.fb_checklist.BASE_URL
import com.example.fb_checklist.DataItem.PostEngineData
import com.example.fb_checklist.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddEngine : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_engine)

        val backBtn: ImageButton = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressed()
        }

        val editTextEngineNumber = findViewById<EditText>(R.id.inputEngineNumber)
        val editTextPlateNumber = findViewById<EditText>(R.id.inputPlateNumber)
        val editTextEngineType = findViewById<EditText>(R.id.inputEngineType)

        // Example usage of the POST function
        val addEngineButton = findViewById<Button>(R.id.saveBtn)
        addEngineButton.setOnClickListener {
            val engineNumber = editTextEngineNumber.text.toString().toIntOrNull()
            val plateNumber = editTextPlateNumber.text.toString()
            val engineType = editTextEngineType.text.toString()

            val newEngine = engineNumber?.let { it1 ->
                PostEngineData(
                    engine_number = it1,
                    plate_number = plateNumber,
                    engine_type = engineType
                )
            }
            if (newEngine != null) {
                createEngine(newEngine)
            }
        }

    }

    private fun createEngine(newEngine: PostEngineData) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        val retrofitAddEngine = apiInterface.createEngine(newEngine)

        retrofitAddEngine.enqueue(object : Callback<PostEngineData> {
            override fun onResponse(call: Call<PostEngineData>, response: Response<PostEngineData>) {
                if (response.isSuccessful) {
                    // Handle successful response
                    val createdEngine = response.body()
                    val message = "Engine created successfully: $createdEngine"
                    Log.d("Engine created", message)
                    Toast.makeText(this@AddEngine, message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@AddEngine, Engines::class.java)
                    startActivity(intent)
                } else {
                    val createdEngine = response.body()
                    val message = "Engine created unsuccessfully: $createdEngine"
                    Log.d("Try Again", message)
                    Toast.makeText(this@AddEngine, message, Toast.LENGTH_SHORT).show()
                    // Show error message to the user
                }
            }

            override fun onFailure(call: Call<PostEngineData>, t: Throwable) {
                Toast.makeText(this@AddEngine , "Something went Wrong" , Toast.LENGTH_SHORT).show()
                Log.d("error Engine", "onFailure: " + t.message)
                // Show error message to the user
            }
        })
    }

}