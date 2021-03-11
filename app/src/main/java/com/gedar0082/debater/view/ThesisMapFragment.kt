package com.gedar0082.debater.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.FragmentThesisMapBinding
import com.gedar0082.debater.model.local.DebateDB
import com.gedar0082.debater.model.local.entity.Thesis
import com.gedar0082.debater.repository.DebateRepository
import com.gedar0082.debater.repository.ThesisRepository
import com.gedar0082.debater.util.NoOpAlgorithm
import com.gedar0082.debater.util.OnSwipeTouchListener
import com.gedar0082.debater.view.adapters.ThesisMapAdapter
import com.gedar0082.debater.viewmodel.ThesisMapViewModel
import com.gedar0082.debater.viewmodel.factory.ThesisMapFactory



class ThesisMapFragment : Fragment() {

    private lateinit var binding: FragmentThesisMapBinding
    private lateinit var thesisMapViewModel: ThesisMapViewModel
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_thesis_map,
            container,
            false
        )
        val debateRepo = DebateRepository(DebateDB.getDatabase(requireContext()).debateDao())
        val thesisRepo = ThesisRepository(DebateDB.getDatabase(requireContext()).thesisDao())
        val factory = ThesisMapFactory(thesisRepo, debateRepo)
        thesisMapViewModel = ViewModelProvider(this, factory).get(ThesisMapViewModel::class.java)
        binding.vm = thesisMapViewModel
        binding.lifecycleOwner = this
        initGraph()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thesisMapViewModel.debateId = arguments?.getLong("id")!!
        Log.e("id", "${thesisMapViewModel.debateId}")
        navController = view.findNavController()
        thesisMapViewModel.context = requireContext()
        thesisMapViewModel.getTheses(thesisMapViewModel.debateId)
        thesisMapViewModel.theses.observe(viewLifecycleOwner, {
            it?.let {
                binding.graph.adapter = ThesisMapAdapter(it) { selected: Thesis ->
                    Log.e("suka", selected.thesisName)
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(context, "suka", duration)
                    openThesis(selected)
                    toast.show()
                }

            }
        })
        thesisMapViewModel.getTheses(thesisMapViewModel.debateId)

    }

    private fun initGraph() {
        binding.graph.setLayout(NoOpAlgorithm())
        displayTempGraph()
    }

    private fun displayTempGraph() {
        binding.graph.adapter =
            ThesisMapAdapter(listOf()) { selected: Thesis ->
                println(selected.thesisName)
            }
    }

    private fun openThesis(thesis: Thesis){
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.thesis_open, null)
        confirm.setView(promptView)
        confirm.setCancelable(true)
        promptView.setOnTouchListener(object: OnSwipeTouchListener(requireContext()){
            override fun onSwipeLeft() {
                navController.navigate(R.id.action_thesisMapFragment_to_argumentMapFragment)

            }
        })
        val textName = promptView.findViewById<TextView>(R.id.thesis_name)
        val textDesc = promptView.findViewById<TextView>(R.id.thesis_desc)
        textName.text = thesis.thesisName
        textDesc.text = thesis.thesisDesc
        confirm.create()
        confirm.show()
    }



}