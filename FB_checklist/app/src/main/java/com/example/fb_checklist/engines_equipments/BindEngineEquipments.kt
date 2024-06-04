package com.example.fb_checklist.engines_equipments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.example.fb_checklist.ApiInterface
import com.example.fb_checklist.DataItem.EngineDataItem
import com.example.fb_checklist.DataItem.EngineEquipmentDataItem
import com.example.fb_checklist.DataItem.EquipmentDataItem
import com.example.fb_checklist.DataItem.PostEngineEquipments
import com.example.fb_checklist.R
import com.example.fb_checklist.BASE_URL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BindEngineEquipments : AppCompatActivity() {
    private var selectedEngineId: Int? = null
    private var headerRow: TableRow? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bind_engine_equipments)

        val backBtn: ImageButton = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressed()
        }

        getEquipmentData()
        getEngineData()
    }


    fun getEeData(id : Int){
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
                        // Display the equipment list
                        displayInsideEngine(equipmentList)
                    } else {
                        displayInsideEngine(equipmentList)
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
                    val adapter = ArrayAdapter(this@BindEngineEquipments, android.R.layout.simple_spinner_item, engineNames)

                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    // Apply the adapter to the spinner
                    engineSpinner.adapter = adapter

                    // Set an item selected listener to handle selection events
                    engineSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                            val selectedEngine = sortedList[position]
                            val engineTextView : TextView = findViewById(R.id.equipmentInside)
                            engineTextView.setText("Equipment Inside E-" + selectedEngine.engine_number.toString())
                            selectedEngineId = selectedEngine.id
                            getEeData(selectedEngine.id)
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

    fun getEquipmentData() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getEquipmentData()

        retrofitData.enqueue(object : Callback<List<EquipmentDataItem>?> {
            override fun onResponse(
                call: Call<List<EquipmentDataItem>?>,
                response: Response<List<EquipmentDataItem>?>
            )
            {
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    displayEquipmentDetails(responseBody)
                }
            }

            override fun onFailure(call: Call<List<EquipmentDataItem>?>, t: Throwable) {
                Log.d("error Engine", "onFailure: " + t.message)
            }
        })
    }

    private fun displayEquipmentDetails(equipmentList: List<EquipmentDataItem>) {
        val equipmentTableLayout = findViewById<TableLayout>(R.id.equipments)

        equipmentList.forEach { equipment ->
            val tableRow = TableRow(this)

            val equipmentNameTextView = TextView(this)
            equipmentNameTextView.text = equipment.equipment_name
            equipmentNameTextView.textSize = 18f
            equipmentNameTextView.setPadding(16, 16, 16, 16)
            tableRow.addView(equipmentNameTextView)

            val quantityEditText = EditText(this)
            quantityEditText.textSize = 18f
            quantityEditText.setPadding(16, 16, 16, 16)
            quantityEditText.inputType = InputType.TYPE_CLASS_NUMBER
            tableRow.addView(quantityEditText)

            val actionBtn = Button(this)
            actionBtn.setText("Add")
            actionBtn.setOnClickListener {
                val engineId = selectedEngineId
                val equipmentId = equipment.id
                val quantity = quantityEditText.text.toString().toIntOrNull()

                if (engineId != null && equipmentId != null && quantity != null) {
                    val newEe = PostEngineEquipments(
                        engine_id = engineId,
                        equipment_id = equipmentId,
                        quantity = quantity
                    )
                    if (newEe != null) {
                        createEngineEquipment(newEe)
                    }
                } else {
                    Log.d("error Engine", "Missing data: engineId=$engineId, equipmentId=$equipmentId, quantity=$quantity")
                }
            }
            tableRow.addView(actionBtn)


            equipmentTableLayout.addView(tableRow)
        }
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

    private fun displayInsideEngine(equipmentList: List<EngineEquipmentDataItem>) {
        val equipmentTableLayout = findViewById<TableLayout>(R.id.insideEngine)
        equipmentTableLayout.removeAllViews()
        createHeaderRow(equipmentTableLayout)

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
            actionBtn.text = "Remove"
            actionBtn.setOnClickListener {
                deleteEe(equipment.id)
            }
            tableRow.addView(actionBtn)

            equipmentTableLayout.addView(tableRow)
        }
    }

    fun createEngineEquipment(newEe : PostEngineEquipments){
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        val retrofitAddEngine = apiInterface.createEngineEquipment(newEe)

        retrofitAddEngine.enqueue(object : Callback<PostEngineEquipments> {
            override fun onResponse(call: Call<PostEngineEquipments>, response: Response<PostEngineEquipments>) {
                if (response.isSuccessful) {
                    val selectedPositionBeforeUpdate = selectedEngineId
                    val createdEngine = response.body()
                    val message = "Engine created successfully: $createdEngine"
                    Log.d("Engine created", message)
                    Toast.makeText(this@BindEngineEquipments, message, Toast.LENGTH_SHORT).show()
                    getEngineData()
                } else {
                    val createdEngine = response.body()
                    val message = "Engine created unsuccessfully: $createdEngine"
                    Log.d("Try Again", message)
                    Toast.makeText(this@BindEngineEquipments, message, Toast.LENGTH_SHORT).show()
                    // Show error message to the user
                }
            }

            override fun onFailure(call: Call<PostEngineEquipments>, t: Throwable) {
                Toast.makeText(this@BindEngineEquipments , "Something went Wrong" , Toast.LENGTH_SHORT).show()
                Log.d("error Engine", "onFailure: " + t.message)
                // Show error message to the user
            }
        })
    }

    private fun deleteEe(equipmentId: Int) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiInterface::class.java)

        val call = retrofitBuilder.deleteEngineEquipment(equipmentId)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    refreshActivity()
                    Log.d("Delete Equipment", "Successfully deleted equipment with ID $equipmentId")
                } else {
                    Log.d("Delete Equipment", "Failed to delete equipment with ID $equipmentId: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("Delete Equipment", "Error occurred: ${t.message}")
            }
        })
    }

    private fun refreshActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }
}