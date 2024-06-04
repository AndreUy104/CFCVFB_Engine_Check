<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('checklist_equipment', function (Blueprint $table) {
            $table->id();
            $table->foreignId('checklist_id')->constrained('checklist')->onDelete('cascade');
            $table->foreignId('ee_id')->constrained('engine_equipments')->onDelete('cascade');
            $table->boolean('status');
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('checklist_equipment');
    }
};
