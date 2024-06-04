<?php

namespace App\Http\Controllers;

use App\Models\Engines;
use Illuminate\Http\Request;

class EngineController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        return Engines::all();
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        try{
            // Validate the request
            $validatedData = $request->validate([
                'engine_number' => 'required|integer',
                'plate_number' => 'required|string|max:255|unique:engines',
                'engine_type' => 'required|string|max:255',
            ]);

            // Create a new user instance and hash the password
            $engine = new Engines();
            $engine->engine_number = $validatedData['engine_number'];
            $engine->plate_number = $validatedData['plate_number'];
            $engine->engine_type = $validatedData['engine_type'];
            $engine->save();
            return response()->json($engine, 201);
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
        $engine = Engines::findOrFail($id);
        return response()->json($engine);

    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id)
    {
        $validatedData = $request->validate([
            'engine_number' => 'required|integer',
            'plate_number' => 'required|string|max:255|unique:engines',
            'engine_type' => 'required|string|max:255',
        ]);

        $engine = Engines::findOrFail($id);

        if( isset( $validatedData['engine_number'] ) ){
            $engine->engine_number = $validatedData['engine_number'];
        }
        if( isset( $validatedData['plate_number'] ) ){
            $engine->plate_number = $validatedData['plate_number'];
        }
        if( isset( $validatedData['engine_type'] ) ){
            $engine->engine_type = $validatedData['engine_type'];
        }
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        $engine = Engines::findOrFail($id);
        $engine->delete();

        return response()->json(['message' => 'Engine deleted successfully']);
    }
}
