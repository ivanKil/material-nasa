package geekbarains.material.ui.todolist

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import geekbarains.nasa.R
import kotlinx.android.synthetic.main.activity_todolist.*
import kotlinx.android.synthetic.main.todolist_item.view.*
import kotlin.math.abs
import kotlin.random.Random

class ToDoListActivity : AppCompatActivity() {
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var adapter: RecyclerActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todolist)
        val data = arrayListOf<Pair<Data, Boolean>>()

        adapter = RecyclerActivityAdapter(
            data,
            object : RecyclerActivityAdapter.OnStartDragListener {
                override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                    itemTouchHelper.startDrag(viewHolder)
                }
            }
        )

        recyclerView.adapter = adapter
        recyclerActivityFAB.setOnClickListener { adapter.appendItem() }
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(adapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)
        adapter.setItems(createItemList().map { it })
    }

    private fun createItemList(): List<Pair<Data, Boolean>> {
        return listOf(
            Pair(Data(1, "Дело1", "Описание 1"), false),
            Pair(Data(2, "Дело2", "Описание 2"), false),
            Pair(Data(3, "Дело3", "Описание 3"), false),
            Pair(Data(4, "Дело4", "Описание 4"), false),
            Pair(Data(5, "Дело5", "Описание 5"), false),
            Pair(Data(6, "Дело6", "Описание 6"), false)
        )
    }
}

class RecyclerActivityAdapter(
    private var data: MutableList<Pair<Data, Boolean>>,
    private val dragListener: OnStartDragListener
) :
    RecyclerView.Adapter<RecyclerActivityAdapter.BaseViewHolder>(), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BaseViewHolder(
            inflater.inflate(R.layout.todolist_item, parent, false) as View
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty())
            super.onBindViewHolder(holder, position, payloads)
        else {
            val combinedChange =
                createCombinedPayload(payloads as List<Change<Pair<Data, Boolean>>>)
            val oldData = combinedChange.oldData
            val newData = combinedChange.newData

            if (newData.first.someText != oldData.first.someText) {
                holder.itemView.todoHeader.text = newData.first.someText
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        data.removeAt(fromPosition).apply {
            data.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, this)
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    fun setItems(newItems: List<Pair<Data, Boolean>>) {
        val result = DiffUtil.calculateDiff(DiffUtilCallback(data, newItems))
        result.dispatchUpdatesTo(this)
        data.clear()
        data.addAll(newItems)
    }

    fun appendItem() {
        data.add(generateItem())
        notifyItemInserted(itemCount - 1)
    }

    private fun generateItem() = Pair(Data(Random.nextInt(), "", ""), true)

    inner class DiffUtilCallback(
        private var oldItems: List<Pair<Data, Boolean>>,
        private var newItems: List<Pair<Data, Boolean>>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].first.id == newItems[newItemPosition].first.id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].first.someText == newItems[newItemPosition].first.someText

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]

            return Change(
                oldItem,
                newItem
            )
        }
    }

    inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ItemTouchHelperViewHolder {

        fun bind(dataItem: Pair<Data, Boolean>) {
            itemView.todoHeader.text = dataItem.first.someText
            itemView.todoDescription.text = dataItem.first.someDescription
            itemView.removeItemImageView.setOnClickListener { removeItem() }
            itemView.descr_group.visibility =
                if (dataItem.second) View.VISIBLE else View.GONE
            itemView.todoHeader.setOnClickListener { toggleText() }
            itemView.dragHandleImageView.setOnTouchListener { _, event ->
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    dragListener.onStartDrag(this)
                }
                false
            }
            setEditAction()
            setSaveAction(dataItem)
        }

        private fun setEditAction() {
            itemView.editButton.setOnClickListener {
                itemView.todoHeaderEdit.visibility = View.VISIBLE
                itemView.todoHeader.visibility = View.GONE
                itemView.todoHeaderEdit.setText(itemView.todoHeader.text.toString())

                itemView.todoDescriptionEdit.visibility = View.VISIBLE
                itemView.todoDescription.visibility = View.GONE
                itemView.todoDescriptionEdit.setText(itemView.todoDescription.text.toString())

                itemView.saveButton.visibility = View.VISIBLE
                itemView.editButton.visibility = View.INVISIBLE
            }
        }

        private fun setSaveAction(dataItem: Pair<Data, Boolean>) {
            itemView.saveButton.setOnClickListener {
                itemView.todoHeaderEdit.visibility = View.GONE
                itemView.todoHeader.visibility = View.VISIBLE
                itemView.todoHeader.setText(itemView.todoHeaderEdit.text.toString())

                itemView.todoDescriptionEdit.visibility = View.GONE
                itemView.todoDescription.visibility = View.VISIBLE
                itemView.todoDescription.setText(itemView.todoDescriptionEdit.text.toString())

                itemView.saveButton.visibility = View.INVISIBLE
                itemView.editButton.visibility = View.VISIBLE

                val ind = data.indexOf(dataItem)
                val id = dataItem.first.id
                data.removeAt(ind)
                data.addAll(
                    ind,
                    listOf(
                        Pair(
                            Data(
                                id,
                                itemView.todoHeader.text.toString(),
                                itemView.todoDescription.text.toString()
                            ), false
                        )
                    )
                )


            }
        }

        private fun removeItem() {
            data.removeAt(layoutPosition)
            notifyItemRemoved(layoutPosition)
        }

        private fun toggleText() {
            data[layoutPosition] = data[layoutPosition].let {
                it.first to !it.second
            }
            notifyItemChanged(layoutPosition)
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }
}

data class Data(
    val id: Int = 0,
    val someText: String = "Text",
    val someDescription: String? = "Description"
)

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemDismiss(position: Int)
}

interface ItemTouchHelperViewHolder {
    fun onItemSelected()
    fun onItemClear()
}

class ItemTouchHelperCallback(private val adapter: RecyclerActivityAdapter) :
    ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(
            dragFlags,
            swipeFlags
        )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMove(source.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
        adapter.onItemDismiss(viewHolder.adapterPosition)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
            itemViewHolder.onItemSelected()
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
        itemViewHolder.onItemClear()
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val width = viewHolder.itemView.width.toFloat()
            val alpha = 1.0f - abs(dX) / width
            viewHolder.itemView.alpha = alpha
            viewHolder.itemView.translationX = dX
        } else {
            super.onChildDraw(
                c, recyclerView, viewHolder, dX, dY,
                actionState, isCurrentlyActive
            )
        }
    }
}
