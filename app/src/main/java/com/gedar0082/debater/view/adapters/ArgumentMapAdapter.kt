package com.gedar0082.debater.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.ArgumentMapNodeBinding
import com.gedar0082.debater.databinding.FragmentArgumentMapBinding
import com.gedar0082.debater.model.local.entity.Argument
import com.gedar0082.debater.model.local.entity.DebateWithArguments
import de.blox.graphview.Graph
import de.blox.graphview.GraphAdapter
import de.blox.graphview.GraphView
import de.blox.graphview.Node

class ArgumentMapAdapter(
    list: List<DebateWithArguments>,
    private val clickListener: (Argument) -> Unit
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
        (viewHolder as ArgumentMapViewHolder).bind(data, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GraphView.ViewHolder {
        val li = LayoutInflater.from(parent.context)
        val binding: ArgumentMapNodeBinding =
            DataBindingUtil.inflate(
                li,
                R.layout.argument_map_node,
                parent,
                false
            )
        return ArgumentMapViewHolder(binding)
    }

    class ArgumentMapViewHolder(private val binding: ArgumentMapNodeBinding):
        GraphView.ViewHolder(binding.root){

            fun bind(data: Any, clickListener: (Argument) -> Unit){
                binding.argumentNodeText.text = if (data is Node) (data.data as Argument).argText else "dump"
                binding.argumentNodeDesc.text = "suck"
                binding.amNode.setOnClickListener {
                    clickListener((data as Node).data as Argument)
                }
            }
    }
}


fun graphInit(list: List<DebateWithArguments>?): Graph {
    val llist: List<Argument>
    if(list == null || list.isEmpty()){
        llist = listOf()
    }else{
        llist = list.first().arguments
    }
    println("size of list = ${llist.size}")
    println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
    val graph = Graph()
    val parent = Node(Argument(0, 0, 0, 0, "problema"))
    graph.addNode(parent)
    for(a in llist){
        val node = Node(a)
        graph.addEdge(parent, node)
    }
    return graph
}