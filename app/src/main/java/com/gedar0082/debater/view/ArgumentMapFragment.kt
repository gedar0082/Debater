package com.gedar0082.debater.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    var topic = ""

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
        argumentMapViewModel.ruleType = arguments?.getInt("ruleType", 1)!!
        argumentMapViewModel.debateId = arguments?.getLong("debate_id", 1)!!
        argumentMapViewModel.thesisId = arguments?.getLong("thesis_id", 1)!!
//        argumentMapViewModel.debateName = arguments?.getString("debate_name", "debate")!!
        val debateId = arguments?.getLong("debate_id", 88)!!
        topic = "/topics/argument$debateId"
        Log.e("fcm", topic)
        FirebaseMessaging.getInstance().subscribeToTopic(topic)

        binding.lifecycleOwner = this
        argumentMapViewModel.getPersonDebate(argumentMapViewModel.debateId)
        initGraph()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()
        argumentMapViewModel.topic = topic
        argumentMapViewModel.context = requireContext()
        argumentMapViewModel.getArguments(argumentMapViewModel.debateId)
        argumentMapViewModel.getPersonDebate(argumentMapViewModel.debateId)
        argumentMapViewModel.navController = navController
        if (argumentMapViewModel.ruleType == 1) binding.argumentOptions.visibility = View.INVISIBLE
        observeNotifications(argumentMapViewModel.debateId)
        argumentMapViewModel.arguments.observe(viewLifecycleOwner, {
            it?.let {
                binding.argumentMapGraph.adapter =
                    ArgumentMapAdapter(it, argumentMapViewModel.debateWithPersons.first().debate, { selected: ArgumentJson ->
                        if (InterScreenController.chooseAnswerArg == 1) {
                            InterScreenController.argumentPressed = selected
                            argumentMapViewModel.createNewArgument(selected)
                        } else {
                            argumentMapViewModel.openArgument(selected)
                        }
                    }, { selected: ArgumentJson ->
                        if (InterScreenController.chooseAnswerArg == 1){
                            Toast.makeText(context, "Try usual click, not long", Toast.LENGTH_SHORT).show()
                        }else{
                            if (argumentMapViewModel.ruleType == 1){
                                Toast.makeText(context, "You can past arguments only to confirm your thesis in classic mode", Toast.LENGTH_SHORT).show()
                            }else{
                                argumentMapViewModel.createNewArgument(selected)
                            }

                        }

                    })
            }
        })

    }

    private fun initGraph() {
        val config = SugiyamaConfiguration.Builder()
            .setLevelSeparation(100)
            .setNodeSeparation(200)
            .build()
        binding.argumentMapGraph.setLayout(SugiyamaAlgorithm(config))
        displayTempGraph()
    }

    /**
     * set adapter as empty plug-graph
     */
    private fun displayTempGraph() {
        binding.argumentMapGraph.adapter = ArgumentMapAdapter(listOf(), argumentMapViewModel.debateWithPersons.first().debate, { selected: ArgumentJson ->
            println(selected.statement)
        }, { selected: ArgumentJson ->
            println(selected.statement)
        })
    }

    /**
     * observe liveData of arguments. When notification is received, in FirebaseService we post to
     * this liveData value and this handler update theses data
     */
    private fun observeNotifications(id: Long) {
        NotificationEvent.argumentEvent.observe(viewLifecycleOwner, {
            argumentMapViewModel.getArguments(id)
        })
    }


    override fun onStop() {
        super.onStop()
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
    }

    override fun onResume() {
        super.onResume()
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }


}