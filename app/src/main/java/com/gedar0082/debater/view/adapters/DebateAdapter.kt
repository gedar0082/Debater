package com.gedar0082.debater.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.DebateCardBinding
import com.gedar0082.debater.model.local.entity.Debate
import com.gedar0082.debater.model.net.pojo.DebateJson

class DebateAdapter(
    private val debates: List<DebateJson>,
    private val clickListener: (DebateJson) -> Unit
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

        fun bind(debate: DebateJson, clickListener: (DebateJson) -> Unit) {
            binding.disNameText.text = debate.name
            binding.disDescriptionText.text = debate.description
            binding.debateDateStart.text = debate.dateStart.toString()
            binding.debateCardCreatedBy.text = "by someone"
            binding.listItemLayout.setOnClickListener {
                clickListener(debate)
            }
        }
    }
}

