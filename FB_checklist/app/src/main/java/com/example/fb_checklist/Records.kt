package com.example.fb_checklist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.fb_checklist.DataItem.ChecklistEquipmentDataItem
import com.example.fb_checklist.DataItem.EngineDataItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Records : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records)

        getEngineData()
    }

    fun getEngineData(){
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

                    val sortedList = responseBody.sortedBy { it.engine_number }

                    val engineSpinner = findViewById<Spinner>(R.id.waterLevelSpinner)

                    // Create a list of engine names
                    val engineNames = sortedList.map { "E- " + it.engine_number.toString() }

                    // Create an ArrayAdapter using the engine names list and a default spinner layout
                    val adapter = ArrayAdapter(this@Records, android.R.layout.simple_spinner_item, engineNames)

                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    // Apply the adapter to the spinner
                    engineSpinner.adapter = adapter

                    // Set an item selected listener to handle selection events
                    engineSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                            val selectedEngine = sortedList[position]
                            getCe(selectedEngine.id)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // Do nothing
                        }
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

    private fun getCe(id : Int){
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getCe(id)

        retrofitData.enqueue(object : Callback<List<ChecklistEquipmentDataItem>> {
            override fun onResponse(
                call: Call<List<ChecklistEquipmentDataItem>>,
                response: Response<List<ChecklistEquipmentDataItem>>
            ) {
                val data = response.body()
                val linearLayout = findViewById<LinearLayout>(R.id.linearLayout)

                if (data != null) {
                    linearLayout.removeAllViews()
                    for (item in data) {
                        // Create and configure TextViews for each field
                        val engineNumberTextView = TextView(this@Records).apply {
                            text = "Engine Number: ${item.engine_number}"
                        }
                        val plateNumberTextView = TextView(this@Records).apply {
                            text = "Plate Number: ${item.plate_number}"
                        }
                        val engineTypeTextView = TextView(this@Records).apply {
                            text = "Engine Type: ${item.engine_type}"
                        }
                        val engineStatusTextView = TextView(this@Records).apply {
                            text = "Engine Status: ${item.engine_status}"
                        }
                        val equipmentNameTextView = TextView(this@Records).apply {
                            text = "Equipment Name: ${item.equipment_name}"
                        }
                        val quantityTextView = TextView(this@Records).apply {
                            text = "Quantity: ${item.quantity}"
                        }
                        val statusTextView = TextView(this@Records).apply {
                            text = "Status: ${item.status}"
                        }
                        val checkedByTextView = TextView(this@Records).apply {
                            text = "Checked By: ${item.checked_by}"
                        }
                        val createdAtTextView = TextView(this@Records).apply {
                            text = "Created At: ${item.created_at.}"
                        }
                        val boarder = TextView(this@Records).apply {
                            text = "-------------------------------------"
                        }

                        // Add TextViews to LinearLayout
                        linearLayout.apply {
                            addView(engineNumberTextView)
                            addView(plateNumberTextView)
                            addView(engineTypeTextView)
                            addView(engineStatusTextView)
                            addView(equipmentNameTextView)
                            addView(quantityTextView)
                            addView(statusTextView)
                            addView(checkedByTextView)
                            addView(createdAtTextView)
                            addView(boarder)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<ChecklistEquipmentDataItem>>, t: Throwable) {
                Log.d("error Engine", "onFailure: " + t.message)
                Toast.makeText(this@Records , "Error Displaying data" , Toast.LENGTH_SHORT).show()
            }
        })
    }
}