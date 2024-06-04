<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Equipments extends Model
{
    use HasFactory;

    protected $guarded = ['id'];

    public function engines()
    {
        return $this->belongsToMany(Engine::class, 'engine_equipment');
    }
}
