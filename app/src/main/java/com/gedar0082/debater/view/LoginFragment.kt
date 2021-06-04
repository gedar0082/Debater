package com.gedar0082.debater.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.FragmentLoginBinding
import com.gedar0082.debater.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*


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
        binding.hintEmailAddress.visibility = View.INVISIBLE
        binding.editTextTextEmailAddress.visibility = View.INVISIBLE
        binding.hintPassword.visibility = View.INVISIBLE
        binding.editTextTextPassword.visibility = View.INVISIBLE
        binding.hintUsername.visibility = View.INVISIBLE
        binding.editTextTextPersonName.visibility = View.INVISIBLE

        val loginObserver = Observer<String>{
            binding.hintEmailAddress.visibility = View.VISIBLE
            binding.editTextTextEmailAddress.visibility = View.VISIBLE
            binding.hintPassword.visibility = View.VISIBLE
            binding.editTextTextPassword.visibility = View.VISIBLE
            binding.btnRegister.visibility = View.INVISIBLE
        }
        loginViewModel.loginLiveData.observe(viewLifecycleOwner, loginObserver)

        val registrationObserver = Observer<String>{
            binding.hintEmailAddress.visibility = View.VISIBLE
            binding.editTextTextEmailAddress.visibility = View.VISIBLE
            binding.hintPassword.visibility = View.VISIBLE
            binding.editTextTextPassword.visibility = View.VISIBLE
            binding.hintUsername.visibility = View.VISIBLE
            binding.editTextTextPersonName.visibility = View.VISIBLE
            btn_login.visibility = View.INVISIBLE
        }
        loginViewModel.registrationLiveData.observe(viewLifecycleOwner, registrationObserver)

        val pref = activity?.getPreferences(Context.MODE_PRIVATE)
        loginViewModel.prefs = pref!!
        loginViewModel.navController = navController
        loginViewModel.nicknameText.postValue((pref.getString("nickname", "") ?: ""))
        loginViewModel.emailText.postValue(pref.getString("email", "") ?: "")
        loginViewModel.password.postValue(pref.getString("password", "") ?: "")

    }



}