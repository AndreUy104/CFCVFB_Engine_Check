package com.example.fb_checklist.engines_equipments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fb_checklist.ApiInterface
import com.example.fb_checklist.BASE_URL
import com.example.fb_checklist.DataItem.EngineEquipmentDataItem
import com.example.fb_checklist.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EngineEquipmentPage : AppCompatActivity() {
    private var headerRow: TableRow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_engine_equipment_page)

        val backBtn: ImageButton = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressed()
        }

        val arraySpinner = arrayOf(
            "Full" , "Mid" , "Low"
        )

        val adapter = ArrayAdapter(this@EngineEquipmentPage, android.R.layout.simple_spinner_item, arraySpinner)

        val waterLvlSpinner : Spinner = findViewById(R.id.waterLevelSpinner)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        waterLvlSpinner.adapter = adapter

        // Get the engine ID from the intent
        val engineId = intent.getIntExtra("ENGINE_ID", -1)
        if (engineId != -1) {
            // Fetch engine details if a valid ID is passed
            getEngineDetails(engineId)
        } else {
            // Handle the case where no valid ID is passed
            Toast.makeText(this, "Invalid Engine ID", Toast.LENGTH_SHORT).show()
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
                        displayEngineDetails(firstItem)

                        // Display the equipment list
                        displayEquipmentDetails(equipmentList)
                    } else {
                        displayEquipmentDetails(equipmentList)
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

    private fun createHeaderRow(equipmentTableLayout: TableLayout) {
        headerRow = TableRow(this)

        val equipmentNameLabel = TextView(this)
        equipmentNameLabel.text = "Equipment Name"
        equipmentNameLabel.textSize = 18f
        equipmentNameLabel.setPadding(8, 8, 8, 8)
        headerRow?.addView(equipmentNameLabel)

        val quantityLabel = TextView(this)
        quantityLabel.text = "Quantity"
        quantityLabel.textSize = 18f
        quantityLabel.setPadding(4, 4, 4, 4)
        headerRow?.addView(quantityLabel)

        val actionLabel = TextView(this)
        actionLabel.text = "Action"
        actionLabel.textSize = 18f
        actionLabel.setPadding(4, 4, 4, 4)
        headerRow?.addView(actionLabel)

        equipmentTableLayout.addView(headerRow)
    }

    private fun displayEquipmentDetails(equipmentList: List<EngineEquipmentDataItem>) {
        val equipmentTableLayout = findViewById<TableLayout>(R.id.equipmentTableLayout)
        equipmentTableLayout.removeAllViews()

        if (headerRow == null) {
            createHeaderRow(equipmentTableLayout)
        }

        if (equipmentList.isEmpty()) {
            val emptyTextView = TextView(this)
            emptyTextView.text = "No equipment inside the engine."
            emptyTextView.textSize = 18f
            emptyTextView.setPadding(16, 16, 16, 16)
            emptyTextView.gravity = Gravity.CENTER
            equipmentTableLayout.addView(emptyTextView)
            return
        }
        equipmentList.forEach { equipment ->
            val tableRow = TableRow(this)

            val equipmentNameTextView = TextView(this)
            equipmentNameTextView.text = equipment.equipment_name
            equipmentNameTextView.textSize = 18f
            equipmentNameTextView.setPadding(16, 16, 16, 16)
            tableRow.addView(equipmentNameTextView)

            val quantityTextView = TextView(this)
            quantityTextView.text = equipment.quantity.toString()
            quantityTextView.textSize = 18f
            quantityTextView.setPadding(16, 16, 16, 16)
            tableRow.addView(quantityTextView)

            val actionBtn = Button(this)
            actionBtn.text = "Check"
            quantityTextView.textSize = 18f
            tableRow.addView(actionBtn)

            equipmentTableLayout.addView(tableRow)
        }
    }
}