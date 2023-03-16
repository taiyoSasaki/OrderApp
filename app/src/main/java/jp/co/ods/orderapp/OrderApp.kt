package jp.co.ods.orderapp

import android.app.Application

class OrderApp : Application() {

    private var orderList: ArrayList<Order> = ArrayList()
    private var orderedList: ArrayList<Order> = ArrayList()

    override fun onCreate() {
        super.onCreate()
    }

    fun addOrder(order: Order) {
        orderList.add(order)
    }

    fun removeOrder(order: Order) {
        orderList.remove(order)
    }

    fun getOrderList() :ArrayList<Order> {
        return orderList
    }

    fun clearOrderList() :ArrayList<Order> {
        orderedList = orderList.clone() as ArrayList<Order>
        orderList.clear()
        return orderList
    }

    companion object {
        private var instance: OrderApp? = null

        fun getInstance() : OrderApp {
            if (instance == null)
                instance = OrderApp()

            return instance!!
        }

    }
}