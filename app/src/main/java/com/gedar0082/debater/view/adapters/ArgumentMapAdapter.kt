package com.gedar0082.debater.view.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.ArgumentMapNodeBinding
import com.gedar0082.debater.model.net.pojo.ArgumentJson
import com.gedar0082.debater.model.net.pojo.DebateJson
import com.gedar0082.debater.util.Util
import de.blox.graphview.*


/**
 * The class accepts an array of data to display and clickListeners to place on each object.
 */
class ArgumentMapAdapter(
    list: List<ArgumentJson>,
    debate : DebateJson,
    private val clickListener: (ArgumentJson) -> Unit,
    private val longClickListener: (ArgumentJson) -> Unit
): GraphAdapter<GraphView.ViewHolder>(graphInit(list, debate)) {

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

                val argumentJson = if (data is Node) (data.data as ArgumentJson) else null
                argumentJson?.let{
                    val argumentText = textCutter(it.title)
                    val argumentTextFormat = argumentText.replace(
                        "Read more", "<font color='#c7934a'>"+"Read more"+"</font>")// temporary hardcoded color
                    binding.argumentNodeText.text = Html.fromHtml(argumentTextFormat)
                    binding.amNode.setOnClickListener {
                        clickListener(argumentJson)
                    }
                    binding.amNode.setOnLongClickListener{
                        longClickListener(argumentJson)
                        return@setOnLongClickListener true
                    }
                    binding.author.text = textCutter(it.person_id?.nickname ?: "")
                    binding.nodeDate.text = Util.getLocalTimeFromGMTTimestamp(it.date_time)
                }

                when(if (data is Node ) (data.data as ArgumentJson).type ?: 1 else 1) {
                    1 -> binding.nodeColor.setBackgroundColor(ResourcesCompat.getColor(binding.root.resources, R.color.blue, null))
                    2 -> binding.nodeColor.setBackgroundColor(ResourcesCompat.getColor(binding.root.resources, R.color.green, null))
                    3 -> binding.nodeColor.setBackgroundColor(ResourcesCompat.getColor(binding.root.resources, R.color.red, null))
                    4 -> binding.nodeColor.setBackgroundColor(ResourcesCompat.getColor(binding.root.resources, R.color.orange, null))
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

/**
 * Method returns graph with calculated edges
 */
fun graphInit(argumentList : List<ArgumentJson>?, debate: DebateJson): Graph{
    val strictArgumentList : List<ArgumentJson>
    var parentName = "Starting discussion"
    var parentDescription = ""
    if (argumentList != null){
//        if (argumentList.isNotEmpty()){
////            parentName = argumentList.first().debate_id!!.name
////            parentDescription = argumentList.first().debate_id!!.description
//
//        }
        parentName = debate.name
        parentDescription = debate.description
    }
    val parentArgument = ArgumentJson(Long.MAX_VALUE, parentName, parentDescription,
        null,null, null, null,
        debate.dateStart, 1)
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
//    nodes.forEach { println("node ${(it.data as ArgumentJson)}" ) }
    return if(edges.isEmpty()){
        graph.addNode(Node(parentArgument))
        graph
    } else{
        graph.addEdges(*edges.toTypedArray())
        graph
    }

}

/**
 * Method finds a parent Node of current node from list of nodes and return it or null
 */
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