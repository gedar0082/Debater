package com.gedar0082.debater.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.FragmentDebateBinding
import com.gedar0082.debater.model.net.notification.NotificationEvent
import com.gedar0082.debater.model.net.notification.Topics
import com.gedar0082.debater.model.net.pojo.DebateJson
import com.gedar0082.debater.view.adapters.DebateAdapter
import com.gedar0082.debater.viewmodel.DebateViewModel
import com.google.firebase.messaging.FirebaseMessaging

class DebateFragment : Fragment() {

    private lateinit var binding: FragmentDebateBinding
    private lateinit var debateViewModel: DebateViewModel
    private lateinit var navController: NavController
    private val topic = "/topics/maindebate"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_debate,
            container,
            false
        )
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
        debateViewModel = ViewModelProvider(this).get(DebateViewModel::class.java)
        binding.debateViewModel = debateViewModel
        binding.lifecycleOwner = this
        initRecyclerView()
        observeNotifications()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        debateViewModel.navController = navController
        debateViewModel.context = requireContext()
        debateViewModel.getDebates()
    }

    private fun initRecyclerView() {
        binding.debateRecycle.layoutManager = LinearLayoutManager(context)
        displayDiscussions()
    }

    /**
     * In class NotificationEvent we have livedata. In Firebase service we post data to it, when
     * receive message, and when data(any string) is posted, work this method and gets data
     */
    private fun observeNotifications(){
        NotificationEvent.serviceEvent.observe(viewLifecycleOwner,{
            _ -> debateViewModel.getDebates()
        })
    }

    private fun displayDiscussions() {
        val observer = Observer<List<DebateJson>> {
            binding.debateRecycle.adapter =
                DebateAdapter(it) { selectedItem: DebateJson ->
                    run {
                        debateViewModel.openDebate(selectedItem)
                        //itemOnClick(selectedItem)
                    }
                }
        }
        debateViewModel.debates.observe(viewLifecycleOwner, observer)
    }

//    private fun itemOnClick(debate: DebateJson) {
//        val bundle: Bundle = bundleOf(Pair("id", debate.id), Pair("name", debate.name))
//        Log.e("tag", "${debate.id}")
//        navController.navigate(R.id.action_debateFragment_to_thesisMapFragment, bundle)
//    }

    override fun onStop() {
        super.onStop()
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
    }

    override fun onResume() {
        super.onResume()
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }


}