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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.FragmentThesisMapBinding
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
    var debateId = 0L
    private var debateName = ""
    private var rule = 0
    var topic = ""

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
        debateId = arguments?.getLong("debate_id")!!
        Log.e("debate_id", debateId.toString())
        topic = "/topics/debate$debateId"
        FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/myTopic")
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
        thesisMapViewModel = ViewModelProvider(this).get(ThesisMapViewModel::class.java)
        binding.vm = thesisMapViewModel
        thesisMapViewModel.topic = topic
        thesisMapViewModel.getPersonDebate(debateId)
        rule = arguments?.getInt("ruleType", 1)!!
        binding.lifecycleOwner = this
        initGraph()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        thesisMapViewModel.debateId = debateId
        debateName = arguments?.getString("name")!!
        Log.e("id", "${thesisMapViewModel.debateId}")
        navController = view.findNavController()
        thesisMapViewModel.navController = navController
        thesisMapViewModel.getTheses(thesisMapViewModel.debateId)
        thesisMapViewModel.debateName = debateName
        thesisMapViewModel.rule = rule
        observeNotifications(thesisMapViewModel.debateId)

        if (rule == 3){
            val bundle = bundleOf(Pair("debate_id", thesisMapViewModel.debateId), Pair("ruleType", rule), Pair("debate_name", debateName))
            navController.navigate(R.id.action_thesisMapFragment_to_argumentMapFragment, bundle)
        }

        thesisMapViewModel.context = requireContext()
        thesisMapViewModel.res = context?.resources!!
        thesisMapViewModel.theses.observe(viewLifecycleOwner, {
            if (it != null){
                if ( it.size %2 == 0){
                    Toast.makeText(context, "The command \"For\" answers", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "The command \"Against\" answers", Toast.LENGTH_SHORT).show()
                }
            }
            it?.let {
                binding.graph.adapter = ThesisMapAdapter(
                    it,
                    thesisMapViewModel.debateWithPersons.first().debate,
                    { selected: ThesisJson ->
                        thesisMapViewModel.openThesis(selected, navController)
                    },
                    { selected: ThesisJson ->
                        if (thesisMapViewModel.checkRights()) {
                            if (!scanningChildren(selected, it)){
                                thesisMapViewModel.createNewThesis(selected, navController)
                            }else Toast.makeText(
                                context,
                                "This thesis has already been answered",
                                Toast.LENGTH_SHORT
                            ).show()


                        } else Toast.makeText(
                            context,
                            "You haven`t rights to write",
                            Toast.LENGTH_SHORT
                        ).show()

                    })

            }
        })
    }

    private fun scanningChildren(thesis: ThesisJson, theses: List<ThesisJson>) : Boolean{
        var hasChild = false
        theses.forEach { if (it.answer != null && it.answer == thesis) hasChild = true }
        return hasChild
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
                println(selected.title)
            }, { selected: ThesisJson ->
                println(selected.title )
            })
    }

    private fun observeNotifications(id: Long){
        NotificationEvent.thesisEvent.observe(viewLifecycleOwner, {
            thesisMapViewModel.getTheses(id)
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