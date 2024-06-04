package com.example.fb_checklist.equipments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.fb_checklist.ApiInterface
import com.example.fb_checklist.DataItem.EquipmentDataItem
import com.example.fb_checklist.DataItem.PostEquipment
import com.example.fb_checklist.R
import com.example.fb_checklist.BASE_URL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EquipmentPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equipment_page)

        val backBtn: ImageButton = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressed()
        }

        val addEquipmentEditText : EditText = findViewById(R.id.addEquipment)
        val addEquipmentBtn : ImageButton = findViewById(R.id.addBtn)
        addEquipmentBtn.setOnClickListener {
            if(addEquipmentEditText.text.isEmpty()){
                Toast.makeText(this , "Please Enter Valid Equipment" , Toast.LENGTH_SHORT).show()
            }else{
                val newEquipment = PostEquipment(
                    equipment_name = addEquipmentEditText.text.toString()
                )
                createEquipment(newEquipment)
                addEquipmentEditText.setText("")
            }
        }
        getEquipmentData()
    }

    fun createEquipment(newEquipment : PostEquipment){
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.createEquipment(newEquipment)

        retrofitData.enqueue(object : Callback<PostEquipment?> {
            override fun onResponse(
                call: Call<PostEquipment?>,
                response: Response<PostEquipment?>
            ) {
                if (response.isSuccessful) {
                    // Handle successful response
                    val message = "Engine created successfully"
                    Log.d("Equipment created", message)
                    Toast.makeText(this@EquipmentPage, message, Toast.LENGTH_SHORT).show()
                    refreshActivity()
                } else {
                    val createdEngine = response.body()
                    val message = "Equipment created unsuccessfully: $createdEngine"
                    Log.d("Try Again", message)
                    Toast.makeText(this@EquipmentPage, message, Toast.LENGTH_SHORT).show()
                    // Show error message to the user
                }
            }

            override fun onFailure(call: Call<PostEquipment?>, t: Throwable) {
                Toast.makeText(this@EquipmentPage , "Something went Wrong" , Toast.LENGTH_SHORT).show()
                Log.d("error Engine", "onFailure: " + t.message)
                // Show error message to the user
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
                response: Response<List<EquipmentDataItem>?>)
            {
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!

                    for (datas in responseBody) {
                        displayEquipmentDetails(datas)
                    }
                }
            }

            override fun onFailure(call: Call<List<EquipmentDataItem>?>, t: Throwable) {
                Log.d("error Engine", "onFailure: " + t.message)
            }
        })
    }

    private fun displayEquipmentDetails(equipmentDetails: EquipmentDataItem) {
        val engineListLayout = findViewById<LinearLayout>(R.id.equipmentList)
        val equipments = TextView(this@EquipmentPage)
        equipments.setText("")
        equipments.text = equipmentDetails.equipment_name

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(16, 16, 16, 16)
        equipments.layoutParams = layoutParams

        engineListLayout.addView(equipments)

        Toast.makeText(this, "Equipments loaded", Toast.LENGTH_SHORT).show()
    }

    private fun refreshActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }
}