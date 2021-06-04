package com.gedar0082.debater.view

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.gedar0082.debater.R
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val pref = getPreferences(Context.MODE_PRIVATE)
        AppCompatDelegate.setDefaultNightMode(pref.getInt("mode", AppCompatDelegate.MODE_NIGHT_NO))

//        navController = findNavController(this, R.id.fragment)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment?
        navController = navHostFragment!!.navController
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, 0, 0
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment?
        val mFrag = navHostFragment!!.childFragmentManager.fragments[0] // получаем фрагмент, активный в данный момент
        when (item.itemId) {
            R.id.item_settings ->{
                if (mFrag is DebateFragment)
                    navController.navigate(R.id.action_debateFragment_to_settingsFragment)
                if (mFrag is ThesisMapFragment)
                    navController.navigate(R.id.action_thesisMapFragment_to_settingsFragment)
                if (mFrag is ArgumentMapFragment)
                    navController.navigate(R.id.action_argumentMapFragment_to_settingsFragment)
            }
            R.id.item_discussions ->{
                if (mFrag is SettingsFragment)
                    navController.navigate(R.id.action_settingsFragment_to_debateFragment)
                if (mFrag is ThesisMapFragment)
                    navController.navigate(R.id.action_thesisMapFragment_to_debateFragment)
                if (mFrag is ArgumentMapFragment)
                    navController.navigate(R.id.action_argumentMapFragment_to_debateFragment)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}