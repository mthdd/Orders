package com.menu.orders

import android.app.Application
import com.menu.orders.model.OrderService

class App : Application() {
    val orderService = OrderService()
}