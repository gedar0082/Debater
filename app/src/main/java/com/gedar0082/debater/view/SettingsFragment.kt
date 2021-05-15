package com.gedar0082.debater.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.gedar0082.debater.R

import android.content.res.Configuration

import androidx.appcompat.app.AppCompatDelegate





class SettingsFragment : Fragment(), View.OnClickListener {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.settings_day_night).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val prefs = activity?.getPreferences(Context.MODE_PRIVATE)
        when (v!!.id) {
            R.id.settings_day_night -> {
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        prefs?.edit()?.putInt("mode", AppCompatDelegate.MODE_NIGHT_NO)?.apply()
                    }
                    Configuration.UI_MODE_NIGHT_NO -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        prefs?.edit()?.putInt("mode", AppCompatDelegate.MODE_NIGHT_YES)?.apply()
                    }
                }
            }
        }
    }

}