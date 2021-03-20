package com.gedar0082.debater.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.FragmentThesisMapBinding
import com.gedar0082.debater.model.local.DebateDB
import com.gedar0082.debater.model.local.entity.Thesis
import com.gedar0082.debater.repository.ArgumentRepository
import com.gedar0082.debater.repository.DebateRepository
import com.gedar0082.debater.repository.ThesisRepository
import com.gedar0082.debater.util.InterScreenController
import com.gedar0082.debater.util.NoOpAlgorithm
import com.gedar0082.debater.util.OnSwipeTouchListener
import com.gedar0082.debater.view.adapters.ThesisMapAdapter
import com.gedar0082.debater.viewmodel.ThesisMapViewModel
import com.gedar0082.debater.viewmodel.factory.ThesisMapFactory



class ThesisMapFragment : Fragment() {

    private lateinit var binding: FragmentThesisMapBinding
    private lateinit var thesisMapViewModel: ThesisMapViewModel
    private lateinit var navController: NavController
    var debateName = ""

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
        val argumentRepo = ArgumentRepository(DebateDB.getDatabase(requireContext()).argumentDao())
        val factory = ThesisMapFactory(thesisRepo, debateRepo, argumentRepo)
        thesisMapViewModel = ViewModelProvider(this, factory).get(ThesisMapViewModel::class.java)
        binding.vm = thesisMapViewModel
        binding.lifecycleOwner = this
        initGraph()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thesisMapViewModel.debateId = arguments?.getLong("id")!!
        debateName = arguments?.getString("name")!!
        Log.e("id", "${thesisMapViewModel.debateId}")
        navController = view.findNavController()
        thesisMapViewModel.context = requireContext()
        thesisMapViewModel.getTheses(thesisMapViewModel.debateId)
        thesisMapViewModel.theses.observe(viewLifecycleOwner, {
            it?.let {
                binding.graph.adapter = ThesisMapAdapter(it, debateName, { selected: Thesis ->
                    thesisMapViewModel.openThesis(selected, navController)
                }, { selected: Thesis ->
                    thesisMapViewModel.createNewThesis(selected, navController)
                })

            }
        })
        thesisMapViewModel.getTheses(thesisMapViewModel.debateId)

    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (InterScreenController.chooseAnswerArg == 2){
            InterScreenController.chooseAnswerArg = 3
            thesisMapViewModel.createNewThesis(InterScreenController.thesisPressed!!, navController)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    private fun initGraph() {
        binding.graph.setLayout(NoOpAlgorithm())
        displayTempGraph()
    }

    private fun displayTempGraph() {
        binding.graph.adapter =
            ThesisMapAdapter(listOf(), debateName, { selected: Thesis ->
                println(selected.thesisName)
            }, { selected: Thesis ->
                println(selected.thesisName)
            })
    }





}