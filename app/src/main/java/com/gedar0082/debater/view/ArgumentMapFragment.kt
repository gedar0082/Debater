package com.gedar0082.debater.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.FragmentArgumentMapBinding
import com.gedar0082.debater.model.net.notification.NotificationEvent
import com.gedar0082.debater.model.net.pojo.ArgumentJson
import com.gedar0082.debater.util.InterScreenController
import com.gedar0082.debater.view.adapters.ArgumentMapAdapter
import com.gedar0082.debater.viewmodel.ArgumentMapViewModel
import com.google.firebase.messaging.FirebaseMessaging
import de.blox.graphview.layered.SugiyamaAlgorithm
import de.blox.graphview.layered.SugiyamaConfiguration

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
        argumentMapViewModel = ViewModelProvider(this).get(ArgumentMapViewModel::class.java)
        binding.vm = argumentMapViewModel
        argumentMapViewModel.debateId = arguments?.getLong("debate_id", 1)!!
        argumentMapViewModel.thesisId = arguments?.getLong("thesis_id", 1)!!
        val topic = "argument${argumentMapViewModel.debateId}"
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$topic")
        argumentMapViewModel.topic = topic
        binding.lifecycleOwner = this
        initGraph()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()
        argumentMapViewModel.context = requireContext()
        argumentMapViewModel.getArguments(argumentMapViewModel.debateId)
        argumentMapViewModel.navController = navController
        observeNotifications(argumentMapViewModel.debateId)
        argumentMapViewModel.arguments.observe(viewLifecycleOwner, {
            it?.let {
                binding.argumentMapGraph.adapter =
                    ArgumentMapAdapter(it, { selected: ArgumentJson ->
                        if (InterScreenController.chooseAnswerArg == 1) {
                            InterScreenController.argumentPressed = selected
                            argumentMapViewModel.createNewArgument(selected)
                        } else {
                            argumentMapViewModel.openArgument(selected)
                        }
                    }, { selected: ArgumentJson ->
                        argumentMapViewModel.createNewArgument(selected)
                    })
            }
        })

    }

    private fun initGraph() {
        val config = SugiyamaConfiguration.Builder()
            .setLevelSeparation(100)
            .setNodeSeparation(100)
            .build()
        binding.argumentMapGraph.setLayout(SugiyamaAlgorithm(config))
        displayTempGraph()
    }

    /**
     * set adapter as empty plug-graph
     */
    private fun displayTempGraph() {
        binding.argumentMapGraph.adapter = ArgumentMapAdapter(listOf(), { selected: ArgumentJson ->
            println(selected.statement)
        }, { selected: ArgumentJson ->
            println(selected.statement)
        })
    }

    /**
     * observe liveData of theses. When notification is received, in FirebaseService we post to
     * this liveData value and this handler update theses data
     */
    private fun observeNotifications(id: Long) {
        NotificationEvent.thesisEvent.observe(viewLifecycleOwner, {
            argumentMapViewModel.getArguments(id)
        })
    }


}