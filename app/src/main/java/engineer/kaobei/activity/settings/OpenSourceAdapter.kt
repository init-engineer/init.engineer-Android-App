package engineer.kaobei.activity.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import engineer.kaobei.R
import engineer.kaobei.model.opensources.OpenSource

/**
 * Created by Kimi.Peng on 2020/6/9.
 * Refactoring [OpenSourceRecyclerViewAdapter] class, also rename to OpenSourceAdapter, remove interface.
 */
class OpenSourceAdapter(private val openSource: List<OpenSource>, val clickListener: (OpenSource) -> Unit) :
    RecyclerView.Adapter<OpenSourceAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_open_source, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return openSource.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(openSource[position])
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tv_name: TextView = itemView.findViewById(R.id.tv_name)
        val tv_author: TextView = itemView.findViewById(R.id.tv_author)
        val tv_url: TextView = itemView.findViewById(R.id.tv_url)

        fun bind(openSource: OpenSource) {
            tv_name.text = openSource.name
            tv_author.text = openSource.author
            tv_url.text = openSource.url
            itemView.setOnClickListener {
                clickListener(openSource)
            }
        }
    }
}