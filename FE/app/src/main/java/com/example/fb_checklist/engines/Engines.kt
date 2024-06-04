package com.example.fb_checklist.engines

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import com.example.fb_checklist.ApiInterface
import com.example.fb_checklist.BASE_URL
import com.example.fb_checklist.DataItem.EngineDataItem
import com.example.fb_checklist.engines_equipments.EngineEquipmentPage
import com.example.fb_checklist.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Engines : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_engines)

        val backBtn: ImageButton = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressed()
        }

        val addEngineBtn: Button = findViewById(R.id.addEngine)
        addEngineBtn.setOnClickListener {
            toAddEngine();
        }

        // Call the function to fetch data from the API
        getMyData()
    }

    private fun getMyData() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getEngineData()

        retrofitData.enqueue(object : Callback<List<EngineDataItem>?> {
            override fun onResponse(
                call: Call<List<EngineDataItem>?>,
                response: Response<List<EngineDataItem>?>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    val engineListLayout = findViewById<LinearLayout>(R.id.engineList)

                    engineListLayout.removeAllViews()
                    val sortedList = responseBody.sortedBy { it.engine_number }

                    for (myData in sortedList) {
                        val engineBtn = Button(this@Engines)
                        engineBtn.text = "E- " + myData.engine_number.toString()
                        engineBtn.textSize = 18f

                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        layoutParams.setMargins(16, 16, 16, 16)
                        engineBtn.layoutParams = layoutParams

                        // Optionally, set an OnClickListener for each button
                        engineBtn.setOnClickListener {
                            val intent = Intent(this@Engines , EngineEquipmentPage::class.java)
                            intent.putExtra("ENGINE_ID", myData.id)
                            startActivity(intent)
                        }

                        // Add the button to the LinearLayout
                        engineListLayout.addView(engineBtn)
                    }
                } else {
                    Log.d("error Engine", "Response unsuccessful or empty: " + response.message())
                }
            }

            override fun onFailure(call: Call<List<EngineDataItem>?>, t: Throwable) {
                Log.d("error Engine", "onFailure: " + t.message)
            }
        })
    }

    private fun toAddEngine(){
        val intent = Intent(this , AddEngine::class.java)
        startActivity(intent)
    }
}
