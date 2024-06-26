package com.example.fb_checklist

import com.example.fb_checklist.DataItem.ChecklistDataItem
import com.example.fb_checklist.DataItem.ChecklistEquipmentData
import com.example.fb_checklist.DataItem.ChecklistEquipmentDataItem
import com.example.fb_checklist.DataItem.EngineDataItem
import com.example.fb_checklist.DataItem.EngineEquipmentDataItem
import com.example.fb_checklist.DataItem.EquipmentDataItem
import com.example.fb_checklist.DataItem.PostChecklist
import com.example.fb_checklist.DataItem.PostChecklistEquipmnet
import com.example.fb_checklist.DataItem.PostEngineData
import com.example.fb_checklist.DataItem.PostEngineEquipments
import com.example.fb_checklist.DataItem.PostEquipment
import com.example.fb_checklist.DataItem.UpdateCEData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiInterface {

    //Engines
    @GET("api/engines")
    fun getEngineData(): Call<List<EngineDataItem>>

    @GET("api/engines/{id}")
    fun getEngine(@Path("id") id: Int) : Call<List<EngineDataItem>>

    @POST("api/engines")
    fun createEngine(@Body newEngine: PostEngineData): Call<PostEngineData>

    //Equipments
    @GET("api/equipments")
    fun getEquipmentData() : Call<List<EquipmentDataItem>>

    @POST("api/equipments")
    fun createEquipment(@Body newEquipment: PostEquipment): Call<PostEquipment>

    //Checklist
    @GET("api/checklist")
    fun getChecklistData() : Call<List<ChecklistDataItem>>

    @POST("api/checklist")
    fun createChecklist(@Body newChecklist : PostChecklist) : Call<ChecklistDataItem>

    //Checklist Equipment
    @GET("api/ce/{id}")
    fun getCe(@Path("id") id : Int) : Call<List<ChecklistEquipmentDataItem>>
    @POST("api/ce")
    fun createCE(@Body newCE : PostChecklistEquipmnet) : Call<PostChecklistEquipmnet>

    @PUT("api/ce/{id}")
    fun updateCE(@Path("id") id : Int , @Body newCE : UpdateCEData) : Call<ChecklistEquipmentData>

    //Engine Equipment
    @GET("api/ee/{id}")
    fun getEeData(@Path("id") id: Int) : Call<List<EngineEquipmentDataItem>>

    @POST("api/ee")
    fun createEngineEquipment(@Body newEngineEquipment : PostEngineEquipments) : Call<PostEngineEquipments>

    @DELETE("api/ee/{id}")
    fun deleteEngineEquipment(@Path("id") id :Int) : Call<Void>
}