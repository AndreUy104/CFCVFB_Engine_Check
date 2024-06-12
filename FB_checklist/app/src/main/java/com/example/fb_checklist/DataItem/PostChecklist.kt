package com.example.fb_checklist.DataItem

data class PostChecklist(
    val engine_id: Int,
    val engine_status: Boolean,
    val checked_by: String,
    val water_level: String,
    val remarks: String
)