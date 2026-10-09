package com.example.introbanco

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "filmes")
data class Filmes (
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val nome: String,
    val desc: String
)