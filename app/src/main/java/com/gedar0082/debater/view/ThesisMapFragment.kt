package com.gedar0082.debater.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.FragmentThesisMapBinding
import com.gedar0082.debater.model.local.DebateDB
import com.gedar0082.debater.model.local.entity.Debate
import com.gedar0082.debater.model.local.entity.DebateWithTheses
import com.gedar0082.debater.model.local.entity.Thesis
import com.gedar0082.debater.repository.DebateRepository
import com.gedar0082.debater.repository.ThesisRepository
import com.gedar0082.debater.view.adapters.ThesisMapAdapter
import com.gedar0082.debater.viewmodel.ThesisMapViewModel
import com.gedar0082.debater.viewmodel.factory.ThesisMapFactory
import de.blox.graphview.GraphView
import de.blox.graphview.tree.BuchheimWalkerAlgorithm
import de.blox.graphview.tree.BuchheimWalkerConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext


class ThesisMapFragment : Fragment() {

    private lateinit var binding: FragmentThesisMapBinding
    private lateinit var thesisMapViewModel: ThesisMapViewModel

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
        thesisMapViewModel.context = requireContext()
        thesisMapViewModel.getTheses(thesisMapViewModel.debateId)
        thesisMapViewModel.theses.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.graph.adapter = ThesisMapAdapter(it){
                        selected : Thesis -> Log.e("suka",selected.thesisName)
                }
            }
        })
        thesisMapViewModel.getTheses(thesisMapViewModel.debateId)

    }

    private fun initGraph(){
        val config : BuchheimWalkerConfiguration = BuchheimWalkerConfiguration.Builder()
            .setSiblingSeparation(100)
            .setLevelSeparation(300)
            .setSubtreeSeparation(300)
            .setOrientation(BuchheimWalkerConfiguration.ORIENTATION_TOP_BOTTOM)
            .build()
        binding.graph.setLayout(BuchheimWalkerAlgorithm(config))
        displayTempGraph()
    }

    private fun displayTempGraph(){
        binding.graph.adapter =
            ThesisMapAdapter(listOf()){
                    selected : Thesis -> println(selected.thesisName)
            }
    }


    private fun displayGraph(){
//        binding.graph.adapter =
//            ThesisMapAdapter(listOf()){
//                    selected : Thesis -> println(selected.thesisName)
//            }
//        val observer = Observer<List<DebateWithTheses>>{
//            binding.graph.adapter =
//                ThesisMapAdapter(it){
//                    //selected : Thesis -> println(selected.thesisName)
//                }
//        }
//        thesisMapViewModel.theses.observe(viewLifecycleOwner, observer)
        //observer.onChanged(thesisMapViewModel.theses.value)
        thesisMapViewModel.theses.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.graph.adapter = ThesisMapAdapter(it){
                        selected : Thesis -> println(selected.thesisName)
                }
            }
        })
    }

    private fun navigate(){
        //TODO
    }


}