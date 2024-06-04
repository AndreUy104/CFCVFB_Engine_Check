<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Engine_equipment extends Model
{
    use HasFactory;

    protected $guarded = ['id'];

    function engine(){
        return $this->belongsTo(engines::class , 'engine_id');
    }

    function equipment(){
        return $this->belongsTo(equipment::class , 'equipment_id');
    }
}
