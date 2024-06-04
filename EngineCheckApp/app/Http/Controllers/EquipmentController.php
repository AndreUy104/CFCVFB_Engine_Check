<?php

namespace App\Http\Controllers;

use App\Models\Equipments;
use Illuminate\Http\Request;

class EquipmentController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        return Equipments::all();
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        try{
            $validate = $request->validate([
                'equipment_name' => 'required|string|max:255|unique:equipments'
            ]);

            $equipment = new Equipments();
            $equipment->equipment_name = $validate['equipment_name'];
            $equipment->save();

            return response()->json($equipment, 201);
        }catch (ValidationException $e){
            return response()->json(['errors' => $e->errors()], 422);
        }catch (Exception $e) {
            return response()->json(['error' => 'User could not be created', 'details' => $e->getMessage()], 404);
        }

    }

    /**
     * Display the specified resource.
     */
    public function show(string $equipment = null)
    {
        if ($equipment !== null) {
            // If $equipment is provided, return the specific equipment item
            $equipmentItem = Equipments::whereRaw('LOWER(equipment_name) LIKE ?', ['%' . strtolower($equipment) . '%'])->firstOrFail();
            return response()->json($equipmentItem);
        } else {
            // If $equipment is not provided, return all equipment items
            $allEquipment = Equipments::all();
            return response()->json($allEquipment);
        }
    }


    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id)
    {
        $validate = $request->validate([
            'equipment_name' => 'required|string|max:255'
        ]);

        $equipment = Equipments::findOrFail($id);

        if( isset( $validate['equipment_name'] ) ){
            $equipment->equipment_name = $validate['equipment_name'];
        }
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        $equipment = Equipments::findOrFail($id);
        $equipment->delete();

        return response()->json(['message' => 'User deleted successfully']);
    }
}
