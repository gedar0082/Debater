package com.gedar0082.debater.view.adapters

import android.text.Html
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.gedar0082.debater.R
import com.gedar0082.debater.databinding.ThesisMapNodeBinding
import com.gedar0082.debater.model.net.pojo.DebateJson
import com.gedar0082.debater.model.net.pojo.ThesisJson
import com.gedar0082.debater.util.Util
import de.blox.graphview.*

/**
 * The class accepts an array of data to display and clickListeners to place on each object.
 */

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
        (viewHolder as SimpleViewHolder).bind(data, clickListener, longClickListener, position)
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

        fun bind(data: Any, clickListener: (ThesisJson) -> Unit, longClickListener: (ThesisJson) -> Unit, position: Int){

            val thesisJson = if (data is Node) (data.data as ThesisJson) else null

            thesisJson?.let {
                val thesisText = textCutter(it.title)
                val thesisTextFormat = thesisText.replace("Read more", "<font color='#c7934a'>"+"Read more"+"</font>")//временно захардкожен цвет
                binding.nodeText.text = Html.fromHtml(thesisTextFormat)
                binding.author.text = String.format(
                    binding.root.resources.getString(R.string.debate_card_created_by), it.person?.nickname ?: "debate")
                binding.nodeDate.text = Util.getLocalTimeFromGMTTimestamp(it.dateTime!!)
                binding.tmNode.setOnClickListener {
                    clickListener(thesisJson)
                }
                binding.tmNode.setOnLongClickListener{
                    longClickListener(thesisJson)
                    return@setOnLongClickListener true
                }
            }

            when(if (data is Node) (data.data as ThesisJson).type else 1) {
                1 -> binding.nodeColor.setBackgroundColor(ResourcesCompat.getColor(binding.root.resources, R.color.blue, null))
                2 -> binding.nodeColor.setBackgroundColor(ResourcesCompat.getColor(binding.root.resources, R.color.green, null))
                3 -> binding.nodeColor.setBackgroundColor(ResourcesCompat.getColor(binding.root.resources, R.color.red, null))
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
//логика распределения узлов в этом месте больше не используется, перенес все в NoOpAlgorithm
fun graphInit(list: List<ThesisJson>?, debate: DebateJson): Graph{
    val lList : List<ThesisJson> = if(list == null || list.isEmpty()){
        listOf()
    }else{
        list
    }

    val parent = Node(
        ThesisJson(0, debate.name, debate.description, "",
          0, null,
        null, null, debate.dateStart, 1)
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

