package com.menu.orders.adapter

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.menu.orders.R
import com.menu.orders.databinding.ItemPersonBinding

import com.menu.orders.model.Order

// DiffUtil, который не вошел в статью:
class OrderDiffUtil(
    private val oldList: List<Order>,
    private val newList: List<Order>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldOrder = oldList[oldItemPosition]
        val newOrder = newList[newItemPosition]
        return oldOrder.id == newOrder.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldOrder = oldList[oldItemPosition]
        val newOrder = newList[newItemPosition]
        return oldOrder == newOrder
    }
}

interface OrderActionListener {
    fun onOrderGetId(order: Order)
    fun onOrderLike(order: Order)
    fun onOrderRemove(order: Order)
    fun onOrderMove(order: Order, moveBy: Int)
}

class OrderAdapter(private val orderActionListener: OrderActionListener) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>(), View.OnClickListener {

    var data: List<Order> = emptyList()
        set(newValue) {
            val orderDiffUtil = OrderDiffUtil(field, newValue)
            val orderDiffUtilResult = DiffUtil.calculateDiff(orderDiffUtil)
            field = newValue
            orderDiffUtilResult.dispatchUpdatesTo(this@OrderAdapter)
        }

    override fun getItemCount(): Int = data.size // Количество элементов в списке данных

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPersonBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)
        binding.more.setOnClickListener(this)
        binding.likedImageView.setOnClickListener(this)

        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = data[position] // Получение человека из списка данных по позиции
        val context = holder.itemView.context

        with(holder.binding) {
            holder.itemView.tag = order
            likedImageView.tag = order
            more.tag = order

            val color =
                if (order.isLiked) R.color.red else R.color.grey // Цвет "сердца", если пользователь был лайкнут

            nameTextView.text = order.name // Отрисовка имени пользователя
            companyTextView.text = order.companyName // Отрисовка компании пользователя
            likedImageView.setColorFilter( // Отрисовка цвета "сердца"
                ContextCompat.getColor(context, color),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            Glide.with(context).load(order.photo)
                .circleCrop() // Отрисовка фотографии пользователя с помощью библиотеки Glide
                .error(R.drawable.ic_order)
                .placeholder(R.drawable.ic_order).into(imageView)
        }
    }

    override fun onClick(view: View) {
        val order: Order = view.tag as Order // Получаем из тэга человека

        when (view.id) {
            R.id.more -> showPopupMenu(view)
            R.id.likedImageView -> orderActionListener.onOrderLike(order)
            else -> orderActionListener.onOrderGetId(order)
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val order = view.tag as Order
        val position = data.indexOfFirst { it.id == order.id }

        popupMenu.menu.add(0, ID_MOVE_UP, Menu.NONE, "Up").apply {
            isEnabled = position > 0
        }
        popupMenu.menu.add(0, ID_MOVE_DOWN, Menu.NONE, "Down").apply {
            isEnabled = position < data.size - 1
        }
        popupMenu.menu.add(0, ID_REMOVE, Menu.NONE, "Remove")

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                ID_MOVE_UP -> orderActionListener.onOrderMove(order, -1)
                ID_MOVE_DOWN -> orderActionListener.onOrderMove(order, 1)
                ID_REMOVE -> orderActionListener.onOrderRemove(order)
            }
            return@setOnMenuItemClickListener true
        }

        popupMenu.show()
    }

    companion object {
        private const val ID_MOVE_UP = 1
        private const val ID_MOVE_DOWN = 2
        private const val ID_REMOVE = 3
    }

    class OrderViewHolder(val binding: ItemPersonBinding) : RecyclerView.ViewHolder(binding.root)
}