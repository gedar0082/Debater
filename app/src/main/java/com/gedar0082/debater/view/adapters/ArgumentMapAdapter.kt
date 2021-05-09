package com.gedar0082.debater.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.ArgumentMapNodeBinding
import com.gedar0082.debater.databinding.FragmentArgumentMapBinding
import com.gedar0082.debater.model.net.pojo.ArgumentJson
import com.gedar0082.debater.util.InterScreenController
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
                binding.argumentNodeDesc.text = "dump"
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


//fun graphInit(list: List<ArgumentJson>?): Graph {
//    val argList: List<ArgumentJson>
//    val parentArg = ArgumentJson(Long.MAX_VALUE, "фыв", "", "","", null, null,null, null, null)
//    val graph = Graph()
//    if(list == null || list.isEmpty()){
//        graph.addNode(Node(parentArg))
//        return graph
//    }else{
//        argList = list
//    }
//    val edges = mutableListOf<Edge>()
//    val nodes = mutableListOf<Node>()
//
//    val newList = listOf(parentArg, *argList.toTypedArray())
//    for (i in newList){
//        nodes.add(Node(i))
//    }
//    for(a in nodes) {
//        val parentEdge = getParentNodeEdge(a, nodes)
//        parentEdge?.let { edges.add(it) }
//    }
//    if (edges.isEmpty()){
//        graph.addNode(Node(parentArg))
//    }else{
//        graph.addEdges(*edges.toTypedArray())
//    }
//    println("edges number = ${edges.size}")
//    return graph
//}
//
//fun getParentNodeEdge(argument: Node, list: List<Node>): Edge?{
//    for (a in list){
//        if ((a.data as ArgumentJson).answer_id == null) return null
//        if ((a.data as ArgumentJson).answer_id!!.id == (argument.data as ArgumentJson).id){ //нулпоинтер здесь, так как когда замковый узел попадает в .answer_id.id!!, у него его нет
//            return Edge(a, argument)
//        }
//    }
//    return null
//
//}

fun graphInit(argumentList : List<ArgumentJson>?): Graph{
    val strictArgumentList : List<ArgumentJson>
    val parentArgument = ArgumentJson(Long.MAX_VALUE, "фыв", "",
        "","", null, null,
        null, null, null)
    val graph = Graph()
    if(argumentList == null || argumentList.isEmpty()){
        graph.addNode(Node(parentArgument))
        return graph
    }
    else strictArgumentList = listOf(parentArgument, *argumentList.toTypedArray())
//    else strictArgumentList = listOf(*argumentList.toTypedArray())
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
//    nodes.forEach {
//        if ((it.data as ArgumentJson) == ((nodes.first()).data as ArgumentJson)) return@forEach
//        if ((node.data as ArgumentJson).answer_id!!.id == (it.data as ArgumentJson).id ) return Edge(node, it)
//    }
    return null
}