package com.example.fb_checklist.DataItem

import java.sql.Time

data class ChecklistEquipmentDataItem(
    val engine_number: Int,
    val engine_status: Int,
    val engine_type: String,
    val equipment_name: String,
    val plate_number: String,
    val quantity: Int,
    val status: Int,
    val checked_by : String,
    val created_at: String
)