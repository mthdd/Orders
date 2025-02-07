// Пакет для хранения моделей данных и сервисов
package com.menu.orders.model

// Импорт необходимых библиотек
import com.github.javafaker.Faker
import java.util.*
import kotlin.collections.ArrayList

// Псевдоним для типа функции-слушателя изменений в списке заказов
typealias OrderListener = (orders: List<Order>) -> Unit

// Основной сервис для работы с заказами
class OrderService {

    // Текущий список заказов
    private var orders = mutableListOf<Order>()

    // Список слушателей изменений
    private var listeners = mutableListOf<OrderListener>()

    // Блок инициализации (выполняется при создании экземпляра)
    init {
        val faker = Faker.instance() // Инициализация генератора тестовых данных

        // Генерация 50 тестовых заказов
        orders = (1..50).map {
            Order(
                id = it.toLong(), // Последовательный ID
                name = faker.name().fullName(), // Случайное имя
                companyName = faker.company().name(), // Случайное название компании
                photo = IMAGES[it % IMAGES.size], // Циклический выбор изображений
                isLiked = false // Изначально не лайкнуто
            )
        }.toMutableList()
    }

    // Получение текущего списка заказов
    fun getOrders(): List<Order> = orders

    // Лайк/дизлайк заказа
    fun likeOrder(order: Order) {
        val index = orders.indexOfFirst { it.id == order.id }
        if (index == -1) return

        orders = ArrayList(orders) // Создание новой копии списка
        orders[index] = orders[index].copy(isLiked = !orders[index].isLiked)
        notifyChanges() // Уведомление слушателей
    }

    // Удаление заказа
    fun removeOrder(order: Order) {
        val index = orders.indexOfFirst { it.id == order.id }
        if (index == -1) return

        orders = ArrayList(orders)
        orders.removeAt(index)
        notifyChanges()
    }

    // Перемещение заказа в списке
    fun moveOrder(order: Order, moveBy: Int) {
        val oldIndex = orders.indexOfFirst { it.id == order.id }
        if (oldIndex == -1) return

        val newIndex = oldIndex + moveBy
        orders = ArrayList(orders)
        Collections.swap(orders, oldIndex, newIndex)
        notifyChanges()
    }

    // Регистрация слушателя
    fun addListener(listener: OrderListener) {
        listeners.add(listener)
        listener.invoke(orders) // Немедленный вызов с текущими данными
    }

    // Удаление слушателя
    fun removeListener(listener: OrderListener) {
        listeners.remove(listener)
    }

    // Уведомление всех слушателей об изменениях
    private fun notifyChanges() = listeners.forEach { it.invoke(orders) }

    // Компаньон-объект для хранения констант
    companion object {
        // Список URL изображений для тестовых данных
        private val IMAGES = mutableListOf(
            "https://images.unsplash.com/photo-1600267185393-e158a98703de?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NjQ0&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            // ... остальные URL
        )
    }
}

// Data-класс для представления заказа
data class Order(
    val id: Long,           // Уникальный идентификатор
    val name: String,       // Имя клиента
    val companyName: String,// Название компании
    val photo: String,      // URL фотографии
    val isLiked: Boolean    // Статус лайка
)