package com.gedar0082.debater.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gedar0082.debater.R
import com.gedar0082.debater.R.layout.one_person
import com.gedar0082.debater.model.net.pojo.PersonRightsJson
import com.gedar0082.debater.model.net.pojo.RightJson

class ThesisOptionsAdapter(private val personList: List<PersonRightsJson>,
                           private val clickListener : (PersonRightsJson) -> Unit)
    :RecyclerView.Adapter<ThesisOptionsAdapter.ThesisOptionsViewHolder>(){
        class ThesisOptionsViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThesisOptionsViewHolder =
        ThesisOptionsViewHolder(LayoutInflater.from(parent.context)
            .inflate(one_person, parent, false))

    override fun onBindViewHolder(holder: ThesisOptionsViewHolder, position: Int) {
        holder.view.apply {
            Log.e("participant", "participant by number $position is ${personList[position].person.email}")
            findViewById<TextView>(R.id.person_email).text = personList[position].person.email
            findViewById<TextView>(R.id.person_name).text = personList[position].person.nickname
            findViewById<TextView>(R.id.person_rights).text = getRightsInString(personList[position].rights)
            setOnClickListener{clickListener(personList[position])}
        }
    }

    override fun getItemCount(): Int = personList.size

    private fun getRightsInString(rights: RightJson): String{
        var result = ""
        if (rights.creator == 1) result += "creator "
        if (rights.referee == 1) result += "referee "
        if (rights.read == 1) result += "read "
        if (rights.write == 1) result += "write"
        return result
    }
}