package com.example.introbanco

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase

@Database(entities = [Filmes::class], version = 1)
abstract class AppDatabase : RoomDatabase(){

    //private static AppDataBase INSTANCE = null; - java

    abstract fun filmesDao(): FilmesDAO

    companion object{

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context) : AppDatabase{

            val temInstance = INSTANCE

            if(temInstance != null){
                return temInstance // se o banco existe, devolve ele
            }else {
                // se nao crio o banco

                synchronized(this){

                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                    ).build()

                    INSTANCE = instance
                    return instance

                }
            }

        }

    }

}