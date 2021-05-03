package com.gedar0082.debater.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.FragmentLoginBinding
import com.gedar0082.debater.viewmodel.LoginViewModel


class LoginFragment : Fragment() {

    private lateinit var binding : FragmentLoginBinding
    private  lateinit var loginViewModel: LoginViewModel
    private  lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.vm = loginViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        val pref = activity?.getPreferences(Context.MODE_PRIVATE)
        loginViewModel.prefs = pref!!
        loginViewModel.navController = navController
        loginViewModel.nicknameText.postValue((pref?.getString("nickname", "") ?: ""))
        loginViewModel.emailText.postValue(pref?.getString("email", "") ?: "")
        loginViewModel.password.postValue(pref?.getString("password", "") ?: "")

    }



}