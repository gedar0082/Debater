package com.gedar0082.debater.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.gedar0082.debater.R
import de.blox.graphview.layered.*
import com.gedar0082.debater.databinding.ArgumentMapNodeBinding
import com.gedar0082.debater.databinding.FragmentArgumentMapBinding
import com.gedar0082.debater.model.local.DebateDB
import com.gedar0082.debater.model.local.entity.Argument
import com.gedar0082.debater.repository.ArgumentRepository
import com.gedar0082.debater.repository.DebateRepository
import com.gedar0082.debater.repository.ThesisRepository
import com.gedar0082.debater.view.adapters.ArgumentMapAdapter
import com.gedar0082.debater.viewmodel.ArgumentMapViewModel
import com.gedar0082.debater.viewmodel.factory.ArgumentMapFactory
import de.blox.graphview.tree.BuchheimWalkerConfiguration

class ArgumentMapFragment : Fragment() {

    private lateinit var binding: FragmentArgumentMapBinding
    private lateinit var argumentMapViewModel: ArgumentMapViewModel
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_argument_map,
            container,
            false
        )
        val db = DebateDB.getDatabase(requireContext())
        val debateRepo = DebateRepository(db.debateDao())
        val thesisRepo = ThesisRepository(db.thesisDao())
        val argumentRepo = ArgumentRepository(db.argumentDao())
        val factory = ArgumentMapFactory(debateRepo, thesisRepo, argumentRepo)
        argumentMapViewModel = ViewModelProvider(this, factory).get(ArgumentMapViewModel::class.java)
        binding.vm = argumentMapViewModel
        binding.lifecycleOwner = this
        initGraph()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        argumentMapViewModel.debateId = arguments?.getLong("debate_id",1)!!
        navController = view.findNavController()
        argumentMapViewModel.context = requireContext()
        argumentMapViewModel.getArguments(argumentMapViewModel.debateId)
        argumentMapViewModel.arguments.observe(viewLifecycleOwner, {
            it?.let {
                binding.argumentMapGraph.adapter = ArgumentMapAdapter(it, { selected: Argument ->
                    openArgument(selected)
                }, { selected: Argument ->
                    argumentMapViewModel.createNewArgument(selected)
                })
            }
        })
        argumentMapViewModel.getArguments(argumentMapViewModel.debateId)
    }

    fun initGraph(){
        val config = SugiyamaConfiguration.Builder()
            .setLevelSeparation(100)
            .setNodeSeparation(100)
            .build()
        binding.argumentMapGraph.setLayout(SugiyamaAlgorithm(config))
        displayTempgraph()
    }

    fun displayTempgraph(){
        binding.argumentMapGraph.adapter = ArgumentMapAdapter(listOf(), { selected: Argument ->
            println(selected.argText)
        }, { selected: Argument ->
            println(selected.argText)
        })
    }

    fun openArgument(argument: Argument){
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.argument_open, null)
        confirm.setView(promptView)
        confirm.setCancelable(true)
        val textName = promptView.findViewById<TextView>(R.id.argument_name)
        textName.text = argument.argText
        confirm.create()
        confirm.show()
    }


}