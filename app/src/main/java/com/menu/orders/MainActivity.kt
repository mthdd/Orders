package com.menu.orders

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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: OrderAdapter // Объект Adapter
    private val orderService: OrderService // Объект OrderService
        get() = (applicationContext as App).orderService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val manager = LinearLayoutManager(this) // LayoutManager
        adapter = OrderAdapter(object : OrderActionListener { // Создание объекта
            override fun onOrderGetId(order: Order) =
                Toast.makeText(this@MainActivity, "Orders ID: ${order.id}", Toast.LENGTH_SHORT).show()

            override fun onOrderLike(order: Order) = orderService.likeOrder(order)

            override fun onOrderRemove(order: Order) = orderService.removeOrder(order)

            override fun onOrderMove(order: Order, moveBy: Int) = orderService.moveOrder(order, moveBy)

        })
        orderService.addListener(listener)

        binding.recyclerView.layoutManager = manager // Назначение LayoutManager для RecyclerView
        binding.recyclerView.adapter = adapter // Назначение адаптера для RecyclerView
    }

    private val listener: OrderListener = {adapter.data = it}
}