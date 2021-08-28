package com.training.foodrecipe

import android.app.Application
import com.training.foodrecipe.datasource.local.RecipeDatabase
import com.training.foodrecipe.datasource.remote.IRecipeApi
import com.training.foodrecipe.datasource.remote.RecipeApiClient
import timber.log.Timber
import timber.log.Timber.DebugTree


/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Wednesday, 18/08/2021 22.39
 * https://gitlab.com/indra-yana
 ****************************************************/

class BaseApplication: Application() {

    companion object {
        @JvmStatic
        lateinit var recipeDB: RecipeDatabase

        @JvmStatic
        lateinit var recipeApi: IRecipeApi
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        recipeDB = RecipeDatabase.initDatabase(this)
        recipeApi = RecipeApiClient.initApi(IRecipeApi::class.java)
    }

}