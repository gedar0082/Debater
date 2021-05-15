package com.gedar0082.debater.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.ArgumentMapNodeBinding
import com.gedar0082.debater.model.net.pojo.ArgumentJson
import de.blox.graphview.*

class ArgumentMapAdapter(
    list: List<ArgumentJson>,
    private val clickListener: (ArgumentJson) -> Unit,
    private val longClickListener: (ArgumentJson) -> Unit
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

            fun bind(data: Any, clickListener: (ArgumentJson) -> Unit, longClickListener: (ArgumentJson) -> Unit){
                binding.argumentNodeText.text = if (data is Node) (data.data as ArgumentJson).statement else "dump"
                binding.argumentNodeDesc.text = if (data is Node) (data.data as ArgumentJson).person_id?.nickname ?: "Arguments" else "dump"
                binding.amNode.setOnClickListener {
                    clickListener((data as Node).data as ArgumentJson)
                }
                binding.amNode.setOnLongClickListener{
                    longClickListener((data as Node).data as ArgumentJson)
                    return@setOnLongClickListener true
                }
            }
    }
}


fun graphInit(argumentList : List<ArgumentJson>?): Graph{
    val strictArgumentList : List<ArgumentJson>
    var parentName = "main arg"
    if (argumentList != null){
        if (argumentList.isNotEmpty()){
            parentName = argumentList.first().debate_id!!.name
        }
    }
    val parentArgument = ArgumentJson(Long.MAX_VALUE, parentName, "",
        "","", null, null,
        null, null, null)
    val graph = Graph()
    if(argumentList == null || argumentList.isEmpty()){
        graph.addNode(Node(parentArgument))
        return graph
    }
    else strictArgumentList = listOf(parentArgument, *argumentList.toTypedArray())
    val edges = mutableListOf<Edge>()
    val nodes = mutableListOf<Node>()

    strictArgumentList.forEach { nodes.add(Node(it)) }
    nodes.forEach {
        getParentEdge(it, nodes)?.let { edge ->
            edges.add(edge)
        }
    }
    return if(edges.isEmpty()){
        graph.addNode(Node(parentArgument))
        graph
    } else{
        graph.addEdges(*edges.toTypedArray())
        graph
    }

}


fun getParentEdge(node : Node, nodes: List<Node>) : Edge?{
    if ((node.data as ArgumentJson).answer_id == null) {
           if ((node.data as ArgumentJson) != (nodes.first().data as ArgumentJson)){
               return Edge(nodes.first(), node)
           }else return null
    }
    for (i in nodes){
        if  ((i.data as ArgumentJson) == ((nodes.first()).data as ArgumentJson)) continue
        if ((node.data as ArgumentJson).answer_id!!.id == (i.data as ArgumentJson).id ) return Edge(i, node)
    }
    return null
}