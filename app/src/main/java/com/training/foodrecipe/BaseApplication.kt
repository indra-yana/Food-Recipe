package com.training.foodrecipe

import android.app.Application
import com.training.foodrecipe.datasource.local.RecipeDatabase

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Wednesday, 18/08/2021 22.39
 * https://gitlab.com/indra-yana
 ****************************************************/

class BaseApplication: Application() {

    companion object {
        @JvmStatic
        lateinit var recipeDB: RecipeDatabase
    }

    override fun onCreate() {
        super.onCreate()
        recipeDB = RecipeDatabase.initDatabase(this)
    }

}