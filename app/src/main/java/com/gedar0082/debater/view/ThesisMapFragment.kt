package com.gedar0082.debater.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.FragmentThesisMapBinding
import com.gedar0082.debater.model.net.notification.FirebaseService
import com.gedar0082.debater.model.net.notification.NotificationEvent
import com.gedar0082.debater.model.net.pojo.ThesisJson
import com.gedar0082.debater.util.InterScreenController
import com.gedar0082.debater.util.NoOpAlgorithm
import com.gedar0082.debater.view.adapters.ThesisMapAdapter
import com.gedar0082.debater.viewmodel.ThesisMapViewModel
import com.google.firebase.messaging.FirebaseMessaging


class ThesisMapFragment : Fragment() {

    private lateinit var binding: FragmentThesisMapBinding
    private lateinit var thesisMapViewModel: ThesisMapViewModel
    private lateinit var navController: NavController
    var debateName = ""
    private var rule = 0

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
        println("######################################################$")
        println("suka blya v rot ebal" + arguments?.getLong("id")!!)
        val debateId = arguments?.getLong("id")!!
        val topic = "/topics/debate$debateId"
        println("topic $topic")
        FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/myTopic")
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
        thesisMapViewModel = ViewModelProvider(this).get(ThesisMapViewModel::class.java)
        binding.vm = thesisMapViewModel
        thesisMapViewModel.topic = topic
        thesisMapViewModel.getPersonDebate(debateId)
        val ruleInt = thesisMapViewModel.debateWithPersons.first().debate.regulations.ruleType
        rule = ruleInt
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
        thesisMapViewModel.getTheses(thesisMapViewModel.debateId)
        observeNotifications(thesisMapViewModel.debateId)

        if (rule == 3){
            val bundle = bundleOf(Pair("debate_id", thesisMapViewModel.debateId))
            navController.navigate(R.id.action_thesisMapFragment_to_argumentMapFragment, bundle)
        }

        thesisMapViewModel.context = requireContext()
        thesisMapViewModel.theses.observe(viewLifecycleOwner, {
            it?.let {
                binding.graph.adapter = ThesisMapAdapter(it, thesisMapViewModel.debateWithPersons.first().debate, { selected: ThesisJson ->
                    thesisMapViewModel.openThesis(selected, navController)
                }, { selected: ThesisJson ->
                    if (thesisMapViewModel.checkRights()){
                        thesisMapViewModel.createNewThesis(selected, navController)
                    }else Toast.makeText(context, "You haven`t rights to write", Toast.LENGTH_SHORT).show()

                })

            }
        })
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (InterScreenController.chooseAnswerArg == 2){
            InterScreenController.chooseAnswerArg = 3
            thesisMapViewModel.createNewThesis(InterScreenController.thesisPressed!!, navController)
        }
    }

    private fun initGraph() {
        binding.graph.setLayout(NoOpAlgorithm())
        displayTempGraph()
    }

    private fun displayTempGraph() {
        binding.graph.adapter =
            ThesisMapAdapter(listOf(), thesisMapViewModel.debateWithPersons.first().debate, { selected: ThesisJson ->
                println(selected.intro)
            }, { selected: ThesisJson ->
                println(selected.intro)
            })
    }

    private fun observeNotifications(id: Long){
        NotificationEvent.thesisEvent.observe(viewLifecycleOwner, {
            _ -> thesisMapViewModel.getTheses(id)
        })
    }

}