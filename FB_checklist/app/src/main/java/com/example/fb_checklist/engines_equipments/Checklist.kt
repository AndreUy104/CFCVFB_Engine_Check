package com.example.fb_checklist.engines_equipments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.fb_checklist.ApiInterface
import com.example.fb_checklist.BASE_URL
import com.example.fb_checklist.DataItem.ChecklistDataItem
import com.example.fb_checklist.DataItem.EngineEquipmentDataItem
import com.example.fb_checklist.DataItem.PostChecklist
import com.example.fb_checklist.R
import com.example.fb_checklist.engines.Engines
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Checklist : AppCompatActivity() {
    private lateinit var engineStatusSpinner: Spinner
    private lateinit var waterLvlSpinner: Spinner
    val arrayWaterLevel = arrayOf(
        "Full" , "Mid" , "Low"
    )

    val arrayEngineStatus = arrayOf("Responsable" , "Not Responsable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checklist)

        val backBtn: ImageButton = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressed()
        }

        engineStatusSpinner = findViewById(R.id.engineStatus)
        waterLvlSpinner = findViewById(R.id.waterLevelSpinner)
        val waterAdapter = ArrayAdapter(this@Checklist, android.R.layout.simple_spinner_item, arrayWaterLevel)
        val engineStatusAdapter = ArrayAdapter(this@Checklist , android.R.layout.simple_spinner_item, arrayEngineStatus)

        engineStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        waterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        engineStatusSpinner.adapter = engineStatusAdapter
        waterLvlSpinner.adapter = waterAdapter

        var selectedWaterLevel: String? = null
        var selectedStatus: Boolean = false
        var status: String? = null

        waterLvlSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedWaterLevel = arrayWaterLevel[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        engineStatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                status = arrayEngineStatus[position]

                if (status == arrayEngineStatus[0]){
                    selectedStatus = true
                } else {
                    selectedStatus = false
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        val remarks: EditText = findViewById(R.id.remarks)
        val checkBy: EditText = findViewById(R.id.editTextText)


        val engineId = intent.getIntExtra("ENGINE_ID", -1)
        if (engineId != -1) {
            // Fetch engine details if a valid ID is passed
            Log.d("Intent Engine" , engineId.toString())
            getEngineDetails(engineId)
        } else {
            // Handle the case where no valid ID is passed
            Toast.makeText(this@Checklist , "Invalid Engine ID", Toast.LENGTH_SHORT).show()
        }

        val nextBtn : Button = findViewById(R.id.nextBtn)
        nextBtn.setOnClickListener {
            val newChecklist = PostChecklist(
                engine_id = engineId,
                engine_status = selectedStatus,
                water_level = selectedWaterLevel ?: "",
                remarks = remarks.text.toString(),
                checked_by = checkBy.text.toString(),
            )
            Log.d("NewChecklist" , newChecklist.toString())
            createChecklist(newChecklist)

        }
    }

    private fun getEngineDetails(id: Int) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getEeData(id)

        retrofitData.enqueue(object : Callback<List<EngineEquipmentDataItem>?> {
            override fun onResponse(
                call: Call<List<EngineEquipmentDataItem>?>,
                response: Response<List<EngineEquipmentDataItem>?>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val equipmentList = response.body()!!
                    if (equipmentList.isNotEmpty()) {
                        // Display the engine details from the first item
                        val firstItem = equipmentList[0]
                        Log.d("Data" , equipmentList.toString())
                        displayEngineDetails(firstItem)

                        // Display the equipment list
                    } else {
                        val emptyTextView : TextView = findViewById(R.id.emptytxt)
                        val nextBtn : Button = findViewById(R.id.nextBtn)
                        nextBtn.isEnabled = false
                        emptyTextView.text = "No equipment inside the engine. Please bind Equipment."
                        emptyTextView.textSize = 18f
                        emptyTextView.setPadding(16, 16, 16, 16)
                        emptyTextView.gravity = Gravity.CENTER
                        Log.d("error Engine", "Empty equipment list")

                    }
                } else {
                    Log.d("error Engine", "Response unsuccessful or empty: " + response.message())
                }
            }

            override fun onFailure(call: Call<List<EngineEquipmentDataItem>?>, t: Throwable) {
                Log.d("error Engine", "onFailure: " + t.message)
            }
        })
    }

    private fun displayEngineDetails(engineDetails: EngineEquipmentDataItem) {
        val engineNumberTextView = findViewById<TextView>(R.id.engineNum)
        val plateNumberTextView = findViewById<TextView>(R.id.plateNum)
        val engineTypeTextView = findViewById<TextView>(R.id.engineType)

        engineNumberTextView.text = "E-" + engineDetails.engine_number.toString()
        plateNumberTextView.text = engineDetails.plate_number.uppercase()
        engineTypeTextView.text = engineDetails.engine_type.uppercase()

        Toast.makeText(this, "Engine details loaded", Toast.LENGTH_SHORT).show()
    }

    private fun createChecklist(checklist : PostChecklist){
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        val retrofitAddChecklist = apiInterface.createChecklist(checklist)

        retrofitAddChecklist.enqueue(object : Callback<ChecklistDataItem?> {
            override fun onResponse(
                call: Call<ChecklistDataItem?>,
                response: Response<ChecklistDataItem?>
            ) {
                if (response.isSuccessful) {
                    // Handle successful response
                    val createdEngine = response.body()
                    val message = "Checklist created successfully: $createdEngine"
                    Toast.makeText(this@Checklist, message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Checklist, EngineEquipmentPage::class.java)
                    intent.putExtra("ENGINE_ID", checklist.engine_id)
                    Log.d("Checklist id" , response.body().toString())
                    Log.d("Id" , response.body()?.id.toString())
                    response.body()?.let { intent.putExtra("CHECKLIST_ID", it.id) }
                    startActivity(intent)
                } else {
                    val createdEngine = response.body()
                    val message = "Checklist created unsuccessfully: $createdEngine"
                    Log.d("Try Again", message)
                    Toast.makeText(this@Checklist, message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ChecklistDataItem?>, t: Throwable) {
                Log.d("error Checklist", "onFailure: " + t.message)

            }
        })

    }

}