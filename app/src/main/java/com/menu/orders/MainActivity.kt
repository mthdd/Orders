// Объявление пакета - указывает расположение файла в структуре проекта
package com.menu.orders

// Импорт необходимых Android-компонентов и библиотек
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.menu.orders.adapter.OrderActionListener
import com.menu.orders.adapter.OrderAdapter
import com.menu.orders.databinding.ActivityMainBinding
import com.menu.orders.model.Order
import com.menu.orders.model.OrderListener
import com.menu.orders.model.OrderService

// Главная Activity приложения, наследуется от AppCompatActivity
class MainActivity : AppCompatActivity() {

    // View Binding объект для доступа к элементам интерфейса
    private lateinit var binding: ActivityMainBinding

    // Адаптер для RecyclerView
    private lateinit var adapter: OrderAdapter

    // Сервис для работы с заказами, получается из кастомного Application класса
    private val orderService: OrderService
        get() = (applicationContext as App).orderService

    // Основной метод создания Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Установка корневого view из binding
        setContentView(binding.root)

        // Создание менеджера компоновки для RecyclerView (вертикальный список)
        val manager = LinearLayoutManager(this)

        // Инициализация адаптера с обработчиком действий
        adapter = OrderAdapter(object : OrderActionListener {
            // Обработчик клика по ID заказа
            override fun onOrderGetId(order: Order) =
                Toast.makeText(
                    this@MainActivity,
                    "Orders ID: ${order.id}",
                    Toast.LENGTH_SHORT
                ).show()

            // Обработчик лайка заказа
            override fun onOrderLike(order: Order) = orderService.likeOrder(order)

            // Обработчик удаления заказа
            override fun onOrderRemove(order: Order) = orderService.removeOrder(order)

            // Обработчик перемещения заказа
            override fun onOrderMove(order: Order, moveBy: Int) = orderService.moveOrder(order, moveBy)
        })

        // Регистрация слушателя изменений в сервисе заказов
        orderService.addListener(listener)

        // Настройка RecyclerView
        binding.recyclerView.layoutManager = manager // Установка менеджера компоновки
        binding.recyclerView.adapter = adapter      // Установка адаптера
    }

    // Слушатель изменений в списке заказов
    private val listener: OrderListener = {
        // Обновление данных адаптера при изменении списка заказов
        adapter.data = it
    }
}