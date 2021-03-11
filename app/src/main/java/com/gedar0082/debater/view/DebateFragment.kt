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
import com.gedar0082.debater.model.local.DebateDB
import com.gedar0082.debater.model.local.entity.Debate
import com.gedar0082.debater.repository.DebateRepository
import com.gedar0082.debater.repository.ThesisRepository
import com.gedar0082.debater.view.adapters.DebateAdapter
import com.gedar0082.debater.viewmodel.DebateViewModel
import com.gedar0082.debater.viewmodel.factory.DebateFactory


class DebateFragment : Fragment() {


    private lateinit var binding: FragmentDebateBinding
    private lateinit var debateViewModel: DebateViewModel
    private lateinit var navController: NavController


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
        val dao = DebateDB.getDatabase(requireContext()).debateDao()
        val repo = DebateRepository(dao)
        val trepo = ThesisRepository(DebateDB.getDatabase(requireContext()).thesisDao())
        val factory = DebateFactory(repo, trepo)
        debateViewModel = ViewModelProvider(this, factory).get(DebateViewModel::class.java)
        binding.debateViewModel = debateViewModel
        binding.lifecycleOwner = this
        initRecyclerView()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        debateViewModel.context = requireContext()
    }

    private fun initRecyclerView(){
        binding.debateRecycle.layoutManager = LinearLayoutManager(context)
        displayDiscussions()
    }

    private fun displayDiscussions(){
        val observer = Observer<List<Debate>>{
            binding.debateRecycle.adapter =
                DebateAdapter(it) { selectedItem: Debate ->
                    run {
                        itemOnClick(selectedItem)
                    }
                }
        }
        debateViewModel.debates.observe(viewLifecycleOwner, observer)
    }

    private fun itemOnClick(debate: Debate){
        val bundle: Bundle = bundleOf(Pair("id", debate.id))
        Log.e("tag", "${debate.id}")
//            debateViewModel.thesisInserter(debate)
        navController.navigate(R.id.action_debateFragment_to_thesisMapFragment, bundle)
    }



}