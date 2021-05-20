package com.gedar0082.debater.view.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.ArgumentMapNodeBinding
import com.gedar0082.debater.model.net.pojo.ArgumentJson
import com.gedar0082.debater.model.net.pojo.ThesisJson
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

                val argumentText = textCutter(if (data is Node) (data.data as ArgumentJson).statement else "nothing")
                val argumentTextFormat = argumentText.replace("Read more", "<font color='#c7934a'>"+"Read more"+"</font>")//временно захардкожен цвет

                binding.argumentNodeText.text = Html.fromHtml(argumentTextFormat)
//                binding.argumentNodeText.text = if (data is Node) (data.data as ArgumentJson).statement else "nothing"
//                binding.argumentNodeDesc.text = if (data is Node) (data.data as ArgumentJson).person_id?.nickname ?: "Arguments" else "nothing"
                binding.amNode.setOnClickListener {
                    clickListener((data as Node).data as ArgumentJson)
                }
                binding.amNode.setOnLongClickListener{
                    longClickListener((data as Node).data as ArgumentJson)
                    return@setOnLongClickListener true
                }

                when(if (data is Node ) (data.data as ArgumentJson).type ?: 1 else 1) {
                    1 -> binding.nodeColor.setBackgroundColor(ResourcesCompat.getColor(binding.root.resources, R.color.grey, null))
                    2 -> binding.nodeColor.setBackgroundColor(ResourcesCompat.getColor(binding.root.resources, R.color.green, null))
                    3 -> binding.nodeColor.setBackgroundColor(ResourcesCompat.getColor(binding.root.resources, R.color.red, null))
                    else -> binding.nodeColor.setBackgroundColor(ResourcesCompat.getColor(binding.root.resources, R.color.grey, null))
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


fun graphInit(argumentList : List<ArgumentJson>?): Graph{
    val strictArgumentList : List<ArgumentJson>
    var parentName = "main arg"
    var parentDescription = "parent description"
    if (argumentList != null){
        if (argumentList.isNotEmpty()){
            parentName = argumentList.first().debate_id!!.name
            parentDescription = argumentList.first().debate_id!!.description
        }
    }
    val parentArgument = ArgumentJson(Long.MAX_VALUE, parentName, parentDescription,
        "","", null, null,
        null, null, null, 1)
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
        return if ((node.data as ArgumentJson) != (nodes.first().data as ArgumentJson)){
            Edge(nodes.first(), node)
        }else null
    }
    for (i in nodes){
        if  ((i.data as ArgumentJson) == ((nodes.first()).data as ArgumentJson)) continue
        if ((node.data as ArgumentJson).answer_id!!.id == (i.data as ArgumentJson).id ) return Edge(i, node)
    }
    return null
}