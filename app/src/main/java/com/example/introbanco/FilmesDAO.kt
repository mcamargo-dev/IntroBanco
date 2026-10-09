package com.example.introbanco

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query

@Dao
interface FilmesDAO {

    @Insert
    fun inserir(filme: Filmes)

    @Query("SELECT * FROM filmes")
    fun buscarTodosFilmes() : List<Filmes>

}