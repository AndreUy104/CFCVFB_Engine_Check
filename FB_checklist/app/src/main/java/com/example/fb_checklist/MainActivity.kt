package com.example.fb_checklist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.fb_checklist.engines.Engines
import com.example.fb_checklist.engines_equipments.BindEngineEquipments
import com.example.fb_checklist.equipments.EquipmentPage

const val BASE_URL = "http://192.168.1.3:8000/"
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val engineCheck: Button = findViewById(R.id.engineCheck)
        val equipments: Button = findViewById(R.id.equipment)
        val ee: Button = findViewById(R.id.ee)
        val records: Button = findViewById(R.id.records)

        engineCheck.setOnClickListener {
            toEngineCheckPage();
        }

        equipments.setOnClickListener {
            toEquipmentPage()
        }

        ee.setOnClickListener {
            toBindPage()
        }
    }

    fun toEngineCheckPage(){
        val intent = Intent(this , Engines::class.java)
        startActivity(intent)
    }

    fun  toEquipmentPage(){
        val intent = Intent(this , EquipmentPage::class.java)
        startActivity(intent)
    }

    fun toBindPage(){
        val intent = Intent(this, BindEngineEquipments::class.java)
        startActivity(intent)
    }
}