package com.gedar0082.debater.view.adapters

import android.view.*
import androidx.databinding.DataBindingUtil
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.ThesisMapNodeBinding
import com.gedar0082.debater.model.net.pojo.DebateJson
import com.gedar0082.debater.model.net.pojo.ThesisJson
import com.gedar0082.debater.util.Util
import de.blox.graphview.*

class ThesisMapAdapter(
    list : List<ThesisJson>,
    debate : DebateJson,
    private val clickListener: (ThesisJson)->Unit,
    private val longClickListener: (ThesisJson)->Unit
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
        (viewHolder as SimpleViewHolder).bind(data, clickListener, longClickListener)
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

        fun bind(data: Any, clickListener: (ThesisJson) -> Unit, longClickListener: (ThesisJson) -> Unit){
            binding.nodeText.text = if (data is Node) stringCutter((data.data as ThesisJson).intro) else "nothing"
            binding.nodeDesc.text = if (data is Node) stringCutter((data.data as ThesisJson).definition ?: "") else "nothing"
            binding.author.text = if (data is Node) stringCutter((data.data as ThesisJson).person?.nickname ?: "debate") else "nothing"
            binding.tmNode.setOnClickListener {
                clickListener((data as Node).data as ThesisJson)
            }
            binding.tmNode.setOnLongClickListener{
                longClickListener((data as Node).data as ThesisJson)
                return@setOnLongClickListener true
            }

        }
    }
}

fun graphInit(list: List<ThesisJson>?, debate: DebateJson): Graph{
    val lList : List<ThesisJson> = if(list == null || list.isEmpty()){
        listOf()
    }else{
        list
    }

    val parent = Node(
        ThesisJson(0, debate.name, debate.description, null, null,
        null, null,  0, null,
        null, null, Util.getCurrentDate())
    )
    println("parent = $parent")
    val leftX = 100.0f
    val rightX = 800.0f
    var levelY = 100.0f
    val verticalOffset = 400.0f
    parent.x = 435.0f
    parent.y = levelY
    val graph = Graph()
    graph.addNode(parent)
    var tempNode = parent
    for ((counter, a) in lList.withIndex()){
        val node = Node(a)

        if (counter % 2 == 0){
            node.x = leftX
            node.y = levelY+verticalOffset
        }else{
            node.x = rightX
            node.y = levelY+verticalOffset
            levelY +=verticalOffset
        }
        graph.addEdge(Edge(tempNode, node))
        tempNode = node

    }
    return graph
}

private fun stringCutter(st: String): String{
    return if (st.length > 15) st.substring(0, 15)
    else st
}