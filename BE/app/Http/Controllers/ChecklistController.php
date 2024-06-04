<?php

namespace App\Http\Controllers;

use App\Models\Checklist;
use Illuminate\Http\Request;

class ChecklistController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
       return Checklist::all();

    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        try{
            $validate = $request->validate([
                'engine_id' => 'required|exists:engines,id' ,
                'engine_status' => 'required|boolean' ,
                'water_level' => 'required|string|max:10' ,
            ]);

            $checklist = new Checklist();
            $checklist->engine_id = $validate['engine_id'];
            $checklist->engine_status = $validate['engine_status'];
            $checklist->water_level = $validate['water_level'];
            if( isset($request->remarks) ){
                $checklist->remarks = $request->remarks;
            } else {
                $checklist->remarks = "No Remarks";
            }
            $checklist->save();

            return response()->json($checklist , 201);
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
        $checklist = Checklist::findOrFail($id);
        return response()->json($checklist);
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id)
    {
        $validate = $request->validate([
            'engine_status' => 'required|boolean' ,
            'water_level' => 'required|string|max:10' ,
        ]);

        $checklist = Checklist::findOrFail($id);

        if( isset( $validate['engine_status'] ) ){
            $checklist->engine_status = $validate['engine_status'];
        }
        if( isset( $validate['water_level'] ) ){
            $checklist->water_level = $validate['water_level'];
        }
        if( isset( $request->remarks ) ){
            $checklist->remarks = $request->remarks;
        }

        return response()->json($checklist);
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        $checklist = Checklist::findOrFail($id);
        $checklist->delete();

        return response()->json(['meesage' , "Checklist Deleted" ]);
    }
}
