<?php

namespace App\Http\Controllers;

use App\Models\Engine;
use App\Models\engine_equipment;
use App\Models\Equipment;
use App\Models\EngineEquipment;
use Illuminate\Http\Request;
use function PHPUnit\Runner\validate;

class Engine_EquipmentController extends Controller
{
    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        $validate = $request->validate([
            'engine_id' => 'required|exists:engines,id' ,
            'equipment_id' => 'required|exists:equipments,id' ,
            'quantity' => 'required|integer'
        ]);

        $ee  = new Engine_equipment();
        $ee->engine_id = $validate['engine_id'];
        $ee->equipment_id = $validate['equipment_id'];
        $ee->quantity = $validate['quantity'];
        $ee->save();

        return response()->json($ee , 201);
    }

    /**
     * Display the specified resource.
     */
    public function show(string $id)
    {
        $equipments = Engine_equipment::where('engine_id' , $id)
            ->join('equipments', 'engine_equipments.equipment_id', '=', 'equipments.id')
            ->join('engines' , 'engine_equipments.engine_id' , '=' , 'engines.id')
            ->select('engine_equipments.id' , 'engines.engine_number', 'engines.plate_number', 'engines.engine_type', 'equipments.equipment_name as equipment_name', 'engine_equipments.quantity')
            ->get();

        return response()->json($equipments);
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        $ee = engine_equipment::findOrFail($id);
        $ee->delete();

        return response()->json(['message' => 'Equipment deleted successfully']);
    }
}
