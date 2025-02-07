// Объявление пакета приложения
package com.menu.orders

// Импорт необходимых классов
import android.app.Application
import com.menu.orders.model.OrderService

// Пользовательский класс приложения, наследуемый от стандартного Application
class App : Application() {
    // Экземпляр сервиса заказов, доступный на уровне всего приложения
    val orderService = OrderService()
}