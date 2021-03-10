package com.gedar0082.debater.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.ThesisMapNodeBinding
import com.gedar0082.debater.model.local.entity.DebateWithTheses
import com.gedar0082.debater.model.local.entity.Thesis
import de.blox.graphview.*


class ThesisMapAdapter(
    list : List<DebateWithTheses>,
    private val clickListener: (Thesis)->Unit,
): GraphAdapter<GraphView.ViewHolder>(graphInit(list)) {


    override fun getCount(): Int {
        return graph.nodes.size
    }

    override fun getItem(position: Int): Any {
        return graph.nodes[position]
    }

    override fun isEmpty(): Boolean {
        return graph.nodes.isEmpty()
    }

    override fun onBindViewHolder(viewHolder: GraphView.ViewHolder, data: Any, position: Int) {
        (viewHolder as SimpleViewHolder).bind(data, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GraphView.ViewHolder {
        val li = LayoutInflater.from(parent.context)
        val binding: ThesisMapNodeBinding =
            DataBindingUtil.inflate(
                li,
                R.layout.thesis_map_node,
                parent,
                false
            )
        return SimpleViewHolder(binding)
    }

    class SimpleViewHolder(private val binding: ThesisMapNodeBinding) :
        GraphView.ViewHolder(binding.root) {

        fun bind(data: Any, clickListener: (Thesis) -> Unit){
            binding.nodeText.text = if (data is Node) (data.data as Thesis).thesisName else "dump"
            binding.nodeDesc.text = if (data is Node) (data.data as Thesis).thesisDesc else "dump"
            binding.tmNode.setOnClickListener {
                clickListener((data as Node).data as Thesis)
            }
        }
    }
}

fun graphInit(list: List<DebateWithTheses>?): Graph{
    val llist : List<Thesis>
    if(list == null || list.isEmpty()){
        llist = listOf()
    }else{
        llist = list.first().theses
    }
    val parent = Node(Thesis(100, 0, "suck", "my balls"))
    val graph = Graph()
    graph.addNode(parent)
    for (a in llist){
        val node = Node(a)
        graph.addEdge(Edge(parent, node))
    }
    return graph
}