package com.example.fb_checklist.DataItem

data class ChecklistDataItem(
    val created_at: String,
    val engine_id: Int,
    val engine_status: Boolean,
    val id: Int,
    val checked_by: String,
    val remarks: String,
    val updated_at: String,
    val water_level: String
)