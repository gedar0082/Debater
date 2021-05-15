package com.gedar0082.debater.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.DebateCardBinding
import com.gedar0082.debater.model.net.pojo.DebateWithPersons

class DebateAdapter(
    private val debates: List<DebateWithPersons>,
    private val clickListener: (DebateWithPersons) -> Unit
) : RecyclerView.Adapter<DebateAdapter.DebateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: DebateCardBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.debate_card,
                parent,
                false
            )
        return DebateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DebateViewHolder, position: Int) {
        holder.bind(debates[position], clickListener)
    }

    override fun getItemCount(): Int {
        return debates.size
    }

    class DebateViewHolder(private val binding: DebateCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(debate: DebateWithPersons, clickListener: (DebateWithPersons) -> Unit) {
            binding.disNameText.text = debate.debate.name
            binding.disDescriptionText.text = debate.debate.description
            binding.debateDateStart.text = debate.debate.dateStart.toString()
            binding.debateCardCreatedBy.text = String.format(
                binding.root.resources.getString(R.string.debate_card_created_by),
                debate.findCreator()?.nickname ?: "nobody")
            binding.listItemLayout.setOnClickListener {
                clickListener(debate)
            }
        }
    }
}

