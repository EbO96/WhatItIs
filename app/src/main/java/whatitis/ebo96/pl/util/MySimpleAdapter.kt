package whatitis.ebo96.pl.util

import android.support.v7.widget.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import whatitis.ebo96.pl.R

class MySimpleAdapter<T>(private val noItemsContainer: ViewGroup,
                         private val item: Int,
                         private val click: ((item: T, position: Int) -> Unit)? = null,
                         private val longClick: ((item: T) -> Unit)? = null,
                         private val onSimpleHolder: ((item: T, view: View) -> Unit)? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val noItemsScreen =
            with(noItemsContainer) {
                LayoutInflater.from(context).inflate(R.layout.no_items, noItemsContainer, false)
            }

    init {
        noItemsContainer.addView(noItemsScreen)
    }

    var items = ArrayList<T>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = items.size.apply {
        noItemsScreen.visibility = if (this == 0) View.VISIBLE else View.GONE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        @Suppress("UNCHECKED_CAST")
        (holder as? BindViewHolder<T>)?.onBind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            MySimpleHolder(LayoutInflater.from(parent.context).inflate(item, parent, false))

    inner class MySimpleHolder(itemView: View) : RecyclerView.ViewHolder(itemView), BindViewHolder<T> {

        override fun onBind(item: T) {
            itemView.apply {
                setOnClickListener {
                    click?.invoke(items[position], position)
                }
                setOnLongClickListener {
                    longClick?.invoke(items[position])
                    true
                }
                onSimpleHolder?.invoke(item, itemView)
            }
        }
    }
}

fun <T> RecyclerView.adapter(item: Int, items: ArrayList<T>, noItemsContainer: ViewGroup, spans: Int = 1, orientation: Int = RecyclerView.VERTICAL, decoration: Boolean = false, viewHolder: ((item: T, view: View) -> Unit)? = null, click: ((item: T, position: Int) -> Unit)? = null, longClick: ((item: T) -> Unit)? = null): MySimpleAdapter<T> = this.let {
    itemAnimator = DefaultItemAnimator()

    layoutManager = if (spans == 1) LinearLayoutManager(this.context, orientation, false) else GridLayoutManager(context, spans)

    val adapter = MySimpleAdapter(noItemsContainer, item, click, longClick, viewHolder).apply {
        this@adapter.adapter = this
        this.items = items
    }
    if (decoration) {
        val dividerItemDecoration = DividerItemDecoration(this.context, orientation)
        addItemDecoration(dividerItemDecoration)
    }
    adapter
}