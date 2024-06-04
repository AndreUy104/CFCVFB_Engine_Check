<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;

class UserController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        return User::all();
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        try{
            $validatedData = $request->validate([
                'name' => 'required|string|max:255' ,
                'unit_number' => 'required|string|max:255',
                'password' => 'required|string|min:8',
            ]);

            $users = new User();
            $users->name = $validatedData['name'];
            $users->unit_number = $validatedData['unit_number'];
            $users->password = $validatedData['password'];
            $users->save();
            return response()->json($users, 201);
        } catch (ValidationException $e){
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
        $users = User::findOrFail($id);
        return response()->json($users);
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id)
    {
        $validatedData = $request->validate([
            'name' => 'required|string|max:255' ,
            'unit_number' => 'required|string|max:255',
            'password' => 'required|string|min:8',
        ]);

        $users = User::findOrFail($id);

        if( isset( $validatedData['name'] ) ){
            $users->name = $validatedData['name'];
        }
        if( isset( $validatedData['unit_number'] ) ){
            $users->name = $validatedData['unit_number'];
        }
        if( isset( $validatedData['password'] ) ){
            $users->name = $validatedData['password'];
        }
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        $users = User::findOrFail($id);
        $users->delete();
        return response()->json(['message' => 'User deleted successfully']);
    }
}
