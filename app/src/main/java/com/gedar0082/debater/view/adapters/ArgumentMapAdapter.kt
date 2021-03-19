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
import de.blox.graphview.*

class ArgumentMapAdapter(
    list: List<DebateWithArguments>,
    private val clickListener: (Argument) -> Unit,
    private val longClickListener: (Argument) -> Unit
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
        (viewHolder as ArgumentMapViewHolder).bind(data, clickListener, longClickListener)
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

            fun bind(data: Any, clickListener: (Argument) -> Unit, longClickListener: (Argument) -> Unit){
                binding.argumentNodeText.text = if (data is Node) (data.data as Argument).argText else "dump"
                binding.argumentNodeDesc.text = "suck"
                binding.amNode.setOnClickListener {
                    clickListener((data as Node).data as Argument)
                }
                binding.amNode.setOnLongClickListener{
                    longClickListener((data as Node).data as Argument)
                    return@setOnLongClickListener true
                }
            }
    }
}


fun graphInit(list: List<DebateWithArguments>?): Graph {
    val argList: List<Argument>
    val parentArg = Argument(Long.MAX_VALUE, 0, 0, 0, "problema")
    val graph = Graph()
    if(list == null || list.isEmpty()){
        graph.addNode(Node(parentArg))
        return graph
    }else{
        argList = list.first().arguments
    }
    val edges = mutableListOf<Edge>()
    val nodes = mutableListOf<Node>()

    val newList = listOf(parentArg, *argList.toTypedArray())
    for (i in newList){
        nodes.add(Node(i))
    }
    for(a in nodes) {
        val parentEdge = getParentNodeEdge(a, nodes)
//        if (parentEdge != null){
//            edges.add(parentEdge)
//        }
        parentEdge?.let { edges.add(it) }
    }
    if (edges.isEmpty()){
        graph.addNode(Node(parentArg))
    }else{
        graph.addEdges(*edges.toTypedArray())
    }
    println("edges number = ${edges.size}")
    return graph
}

fun getParentNodeEdge(argument: Node, list: List<Node>): Edge?{
    for (a in list){
        if ((a.data as Argument).aId == (argument.data as Argument).answerTo){
            return Edge(a, argument)
        }
    }
    return null

}