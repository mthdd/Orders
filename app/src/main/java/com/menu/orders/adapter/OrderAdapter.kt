// Пакет для хранения классов адаптеров
package com.menu.orders.adapter

// Импорт необходимых компонентов
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

// Класс для вычисления различий между списками заказов
class OrderDiffUtil(
    private val oldList: List<Order>,
    private val newList: List<Order>
) : DiffUtil.Callback() {
    // Размер старого списка
    override fun getOldListSize(): Int = oldList.size

    // Размер нового списка
    override fun getNewListSize(): Int = newList.size

    // Проверка на одинаковость элементов по ID
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    // Проверка полного совпадения содержимого элементов
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

// Интерфейс для обработки действий с заказами
interface OrderActionListener {
    fun onOrderGetId(order: Order)  // Получение ID заказа
    fun onOrderLike(order: Order)   // Лайк заказа
    fun onOrderRemove(order: Order) // Удаление заказа
    fun onOrderMove(order: Order, moveBy: Int) // Перемещение заказа
}

// Основной класс адаптера для RecyclerView
class OrderAdapter(private val orderActionListener: OrderActionListener) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>(), View.OnClickListener {

    // Список данных с автоматическим обновлением через DiffUtil
    var data: List<Order> = emptyList()
        set(newValue) {
            val diff = OrderDiffUtil(field, newValue) // Сравнение списков
            val diffResult = DiffUtil.calculateDiff(diff) // Вычисление различий
            field = newValue // Обновление данных
            diffResult.dispatchUpdatesTo(this) // Анимированное обновление
        }

    // Количество элементов в списке
    override fun getItemCount(): Int = data.size

    // Создание ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPersonBinding.inflate(inflater, parent, false)

        // Установка обработчиков кликов
        binding.root.setOnClickListener(this)
        // Закомментированные элементы (возможно для будущего использования)
        // binding.more.setOnClickListener(this)
        // binding.likedImageView.setOnClickListener(this)

        return OrderViewHolder(binding)
    }

    // Привязка данных к элементу списка
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = data[position]
        val context = holder.itemView.context

        with(holder.binding) {
            // Сохранение объекта заказа в тег элемента
            holder.itemView.tag = order

            // Установка цвета для иконки лайка
            val colorRes = if (order.isLiked) R.color.red else R.color.grey

            // Заполнение данных
            nameTextView.text = order.name
            companyTextView.text = order.companyName

            // Загрузка изображения с помощью Glide
            Glide.with(context)
                .load(order.photo)
                .circleCrop() // Круглое обрезание изображения
                .error(R.drawable.ic_order) // Заглушка при ошибке
            // .placeholder(R.drawable.ic_order) // Заглушка при загрузке
            // .into(imageView) // Указание ImageView (раскомментировать когда будет готово)
        }
    }

    // Обработка кликов по элементам
    override fun onClick(view: View) {
        val order = view.tag as Order

        when (view.id) {
            // Обработка дополнительных кнопок (закомментировано)
            // R.id.more -> showPopupMenu(view)
            // R.id.likedImageView -> orderActionListener.onOrderLike(order)
            else -> orderActionListener.onOrderGetId(order) // Основной клик по элементу
        }
    }

    // Показать контекстное меню
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val order = view.tag as Order
        val position = data.indexOfFirst { it.id == order.id }

        // Настройка пунктов меню
        popupMenu.menu.add(0, ID_MOVE_UP, Menu.NONE, "Up").apply {
            isEnabled = position > 0 // Включено только если не первый элемент
        }
        popupMenu.menu.add(0, ID_MOVE_DOWN, Menu.NONE, "Down").apply {
            isEnabled = position < data.size - 1 // Включено только если не последний
        }
        popupMenu.menu.add(0, ID_REMOVE, Menu.NONE, "Remove")

        // Обработка выбора пункта меню
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                ID_MOVE_UP -> orderActionListener.onOrderMove(order, -1) // Вверх
                ID_MOVE_DOWN -> orderActionListener.onOrderMove(order, 1) // Вниз
                ID_REMOVE -> orderActionListener.onOrderRemove(order) // Удалить
            }
            true
        }

        popupMenu.show()
    }

    // Константы для идентификации пунктов меню
    companion object {
        private const val ID_MOVE_UP = 1
        private const val ID_MOVE_DOWN = 2
        private const val ID_REMOVE = 3
    }

    // ViewHolder для кеширования элементов интерфейса
    class OrderViewHolder(val binding: ItemPersonBinding) : RecyclerView.ViewHolder(binding.root)
}