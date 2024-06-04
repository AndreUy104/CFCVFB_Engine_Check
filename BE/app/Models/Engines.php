<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Engines extends Model
{
    use HasFactory;

    protected $guarded = ['id'];

    public function equipments()
    {
        return $this->belongsToMany(Equipment::class, 'engine_equipment');
    }
}
