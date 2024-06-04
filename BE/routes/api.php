<?php

use Illuminate\Support\Facades\Route;

Route::apiResource('users' , \App\Http\Controllers\UserController::class);
Route::apiResource('engines' , \App\Http\Controllers\EngineController::class);
Route::apiResource('equipments' , \App\Http\Controllers\EquipmentController::class);
Route::apiResource('ee' , \App\Http\Controllers\Engine_EquipmentController::class);
Route::apiResource('checklist' , \App\Http\Controllers\ChecklistController::class);
Route::apiResource('ce' , \App\Http\Controllers\Checklist_EquipmentController::class);
