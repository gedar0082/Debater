package com.gedar0082.debater.view.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.DebateCardBinding
import com.gedar0082.debater.model.net.pojo.DebateWithPersons

/**
 * The class accepts an array of data to display and clickListener to place on each object.
 */
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
            val debateName = textCutter(debate.debate.name)
            val nDebateName = debateName.replace("Read more", "<font color='#c7934a'>"+"Read more"+"</font>")//временно захардкожен цвет
            binding.disNameText.text = Html.fromHtml(nDebateName)
//            binding.disDescriptionText.text = debate.debate.description
            binding.debateDateStart.text = debate.debate.dateStart.toString()
            binding.debateCardCreatedBy.text = String.format(
                binding.root.resources.getString(R.string.debate_card_created_by),
                debate.findCreator()?.nickname ?: "nobody")
            binding.listItemLayout.setOnClickListener {
                clickListener(debate)
            }
        }

        private fun textCutter(string: String): String{
            return if (string.length>120) String.format(
                binding.root.resources.getString(R.string.debate_string_cutter),
                string.substring(0, 120))
            else string

        }
    }


}

