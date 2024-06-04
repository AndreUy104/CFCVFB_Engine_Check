<?php

namespace App\Http\Controllers;

use App\Models\ChecklistEquipment;
use Illuminate\Http\Request;
use function Laravel\Prompts\select;

class Checklist_EquipmentController extends Controller
{
    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        try{
            $validate = $request->validate([
                'checklist_id' => 'required|exists:checklist,id',
                'ee_id' => 'required|exists:engine_equipments,id' ,
                'status' => 'required|boolean'
            ]);

            $ce = new ChecklistEquipment();
            $ce->checklist_id = $validate['checklist_id'];
            $ce->ee_id = $validate['ee_id'];
            $ce->status = $validate['status'];
            $ce->save();

            return response()->json($ce);

        }catch (ValidationException $e){
            return response()->json(['errors' => $e->errors()], 422);
        }catch (Exception $e) {
            return response()->json(['error' => 'User could not be created', 'details' => $e->getMessage()], 404);
        }
    }

    /**
     * Display the specified resource.
     */
    public function show(string $id)
    {
        $ce = ChecklistEquipment::findOrFail($id)
            ->join('checklist', 'checklist_equipment.checklist_id', '=', 'checklist.id')
            ->join('engine_equipments' , 'checklist_equipment.ee_id' , '=' , 'engine_equipments.id')
            ->join('engines' , 'checklist.engine_id' , '=' , 'engines.id')
            ->join('equipments' , 'engine_equipments.equipment_id' , '=' , 'equipments.id')
            ->select(
                'engines.engine_number' ,
                'engines.plate_number' ,
                'engines.engine_type' ,
                'engines.engine_status' ,
                'equipments.equipment_name' ,
                'engine_equipments.quantity' ,
                'checklist_equipment.status'
            )
            ->get();
        return response()->json($ce);
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        //
    }
}
