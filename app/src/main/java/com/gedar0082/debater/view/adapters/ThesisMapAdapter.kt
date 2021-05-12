package com.gedar0082.debater.view.adapters

import android.view.*
import androidx.databinding.DataBindingUtil
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.ThesisMapNodeBinding
import com.gedar0082.debater.model.net.pojo.DebateJson
import com.gedar0082.debater.model.net.pojo.ThesisJson
import com.gedar0082.debater.model.net.pojo.ThesisJsonRaw
import com.gedar0082.debater.util.CurrentUser
import com.gedar0082.debater.util.Util
import de.blox.graphview.*
import java.sql.Timestamp
import java.text.SimpleDateFormat

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
            binding.nodeText.text = if (data is Node) stringCutter((data.data as ThesisJson).intro) else "dump"
            binding.nodeDesc.text = if (data is Node) stringCutter((data.data as ThesisJson).definition ?: "") else "dump"
            binding.author.text = if (data is Node) stringCutter((data.data as ThesisJson).person?.nickname ?: "debate") else "dump"
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


/*
функция должна быть переписана, так как в данный момент она просто подряд достаёт данные из листа
и друг за другом их расставляет по цепочке делая связи. Сейчас лень, потом понадобится
в будущем распределение по пикселям надо будет вынести в алгоритм, а присваивать тезисы узлам здесь
 */
fun graphInit(list: List<ThesisJson>?, debate: DebateJson): Graph{
    val llist : List<ThesisJson>
    if(list == null || list.isEmpty()){
        llist = listOf()
    }else{
        llist = list
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
//вынести это говно в noOpAlgo
    val graph = Graph()
    graph.addNode(parent)
    var tempNode = parent
    var counter = 0
    for (a in llist){
        val node = Node(a)

        if (counter % 2 == 0){
            node.x = leftX
            node.y = levelY+verticalOffset
        }else{
            node.x = rightX
            node.y = levelY+verticalOffset
            levelY +=verticalOffset
        }
        counter++
        graph.addEdge(Edge(tempNode, node))
        tempNode = node

    }
    return graph
}

private fun stringCutter(st: String): String{
    return if (st.length > 15) st.substring(0, 15)
    else st
}