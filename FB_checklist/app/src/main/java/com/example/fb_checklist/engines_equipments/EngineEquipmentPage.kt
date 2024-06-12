package com.example.fb_checklist.engines_equipments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fb_checklist.ApiInterface
import com.example.fb_checklist.BASE_URL
import com.example.fb_checklist.DataItem.ChecklistEquipmentData
import com.example.fb_checklist.DataItem.EngineEquipmentDataItem
import com.example.fb_checklist.DataItem.PostChecklistEquipmnet
import com.example.fb_checklist.DataItem.UpdateCEData
import com.example.fb_checklist.MainActivity
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

        // Get the engine ID from the intent
        val engineId = intent.getIntExtra("ENGINE_ID", -1)
        if (engineId != -1) {
            // Fetch engine details if a valid ID is passed
            getEngineDetails(engineId)
        } else {
            // Handle the case where no valid ID is passed
            Toast.makeText(this, "Invalid Engine ID", Toast.LENGTH_SHORT).show()
        }

        val doneBtn : Button = findViewById(R.id.doneBtn)
        doneBtn.setOnClickListener {
            val intent = Intent(this@EngineEquipmentPage, MainActivity::class.java)
            startActivity(intent)
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
            actionBtn.setOnClickListener {
                if(actionBtn.text == "Check"){
                    val newCe = PostChecklistEquipmnet(
                        checklist_id = intent.getIntExtra("CHECKLIST_ID", -1) ,
                        ee_id = equipment.id,
                        status = 1
                    )
                    createCE(newCe)
                    actionBtn.text = "Uncheck"
                } else{
                    val updateCE = UpdateCEData(
                        status = false
                    )
                    updateCE(equipment.id , updateCE)
                }
            }

            equipmentTableLayout.addView(tableRow)
        }
    }

    private fun createCE(ce : PostChecklistEquipmnet){
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        val retrofitAddCe = apiInterface.createCE(ce)

        retrofitAddCe.enqueue(object : Callback<PostChecklistEquipmnet?> {
            override fun onResponse(
                call: Call<PostChecklistEquipmnet?>,
                response: Response<PostChecklistEquipmnet?>
            ) {
                if (response.isSuccessful) {
                    // Handle successful response
                    val createdEngine = response.body()
                    val message = "Saved Changes"
                    Toast.makeText(this@EngineEquipmentPage, message, Toast.LENGTH_SHORT).show()
                }else {
                    val message = "Try Again"
                    Log.d("Try Again", message)
                    Toast.makeText(this@EngineEquipmentPage, message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PostChecklistEquipmnet?>, t: Throwable) {
                Log.d("error Checklist", "onFailure: " + t.message)
                Toast.makeText(this@EngineEquipmentPage, "Something went Wrong", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun updateCE(id: Int, updatedCE: UpdateCEData) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        val retrofitUpdateCe = apiInterface.updateCE(id , updatedCE)

        retrofitUpdateCe.enqueue(object : Callback<ChecklistEquipmentData> {
            override fun onResponse(call: Call<ChecklistEquipmentData>, response: Response<ChecklistEquipmentData>) {
                if (response.isSuccessful) {
                    val updatedChecklist = response.body()
                    val message = "Checklist updated successfully: $updatedChecklist"
                    Toast.makeText(this@EngineEquipmentPage, message, Toast.LENGTH_SHORT).show()

                    // You can handle the updated data here if needed
                    // For example, navigate to another activity or update the UI
                } else {
                    // Handle error
                    Toast.makeText(this@EngineEquipmentPage, "Failed to update checklist", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ChecklistEquipmentData>, t: Throwable) {
                // Handle failure
                Toast.makeText(this@EngineEquipmentPage, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}