package com.training.foodrecipe

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.postDelayed
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.gauravk.bubblenavigation.BubbleNavigationLinearView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.snackbar.Snackbar
import com.training.foodrecipe.databinding.FragmentRecipeBinding
import com.training.foodrecipe.helper.ConnectivityHelper
import com.training.foodrecipe.helper.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private var currentNavController: LiveData<NavController>? = null
    private var backPressedOnce = false

    private lateinit var fabAction: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ConnectivityHelper(applicationContext).observe(this, Observer { isConnected ->
            if (isConnected) {
//                networkStatusContainer.visibility = View.GONE
                layoutConnected.visibility = View.VISIBLE
                layoutDisconnected.visibility = View.GONE
            } else {
//                networkStatusContainer.visibility = View.VISIBLE
                layoutConnected.visibility = View.GONE
                layoutDisconnected.visibility = View.VISIBLE
            }
        })

        fabAction = findViewById(R.id.fabCreate)
        fabAction.setOnClickListener {
            Toast.makeText(this, "Fab Clicked!", Toast.LENGTH_SHORT).show()
        }

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }  // Else, need to wait for onRestoreInstanceState
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        val navGraphIds = listOf(
            R.navigation.nav_home,
            R.navigation.nav_article,
            R.navigation.nav_favourite
        )

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavBar.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.navHostContainer,
            intent = intent
        )

        /*
        bottomNavBar.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_article -> {
                    Toast.makeText(this, "Article", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_favourite -> {
                    Toast.makeText(this, "Favourite", Toast.LENGTH_SHORT).show()
                }
            }

            return@setOnItemSelectedListener false
        }
        */

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, Observer {
//            setupActionBarWithNavController(it)
        })

        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.navHostContainer)

        // Check if the current destination is actually the start destination (Home screen)
        if (navController.currentDestination?.id == R.id.homeFragment) {
            // Check if back is already pressed. If yes, then exit the app.
            if (backPressedOnce) {
                super.onBackPressed()
                return
            }

            backPressedOnce = true
            Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show()

            Handler().postDelayed(2000 ) {
                backPressedOnce = false
            }
        } else {
            super.onBackPressed()
        }
    }

    fun hideFabAction() {
        fabAction.visibility = View.GONE
    }

    fun showFabAction() {
        fabAction.visibility = View.VISIBLE
    }

    fun showBottomNavigation() {
        bottomNavBar.visibility = View.VISIBLE
    }

    fun hideBottomNavigation() {
        bottomNavBar.visibility = View.GONE
    }

    // TODO: Add this on detail fragment to show or hide bottom navigation
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        (activity as MainActivity).hideBottomNavigation()
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        (activity as MainActivity).showBottomNavigation()
//    }


//    fun setupNavigationBar() {
//        val navigationBar = findViewById<BubbleNavigationLinearView>(R.id.bottomNavBar)
//
//        navigationBar.setNavigationChangeListener { view, position ->
//            when (position) {
//                0 -> {
//                    val action = ArticleFragmentDirections.actionArticleFragmentToRecipeFragment()
//                    findNavController(this, R.id.navFragment).navigate(action)
//
//                    Log.d(TAG, "setupNavigationBar: Home")
//                }
//                1 -> {
//                    val action = RecipeFragmentDirections.actionRecipeFragmentToArticleFragment()
//                    findNavController(this, R.id.navFragment).navigate(action)
//
//                    Log.d(TAG, "setupNavigationBar: Article")
//                }
//                2 -> {
//                    Log.d(TAG, "setupNavigationBar: Favourite")
//
//                }
//            }
//        }
//
//    }
}