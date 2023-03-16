package jp.co.ods.orderapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.list_order.view.*

class OrderListAdapter(context: Context)  : BaseAdapter(){
    companion object {
        private val TYPE_TITLE = 0
        private val TYPE_OEDER = 1
    }

    private var mOrderList = ArrayList<Order>()
    private var mLayoutInflater: LayoutInflater? = null

    init {
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    //注文取り消しボタン
    var onClickCancel: ((Order) -> Unit)? = null

    override fun getCount(): Int {
        return 1 + mOrderList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_TITLE
        } else {
            TYPE_OEDER
        }
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Any {
        return mOrderList[position]
    }

    override fun getItemId(position: Int): Long {
        return 1 + position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view

        if (getItemViewType(position) == TYPE_TITLE) {
            if (convertView == null) {
                convertView = mLayoutInflater!!.inflate(R.layout.list_order_top, parent, false)!!
            }
        } else {
            if (convertView == null) {
                convertView = mLayoutInflater!!.inflate(R.layout.list_order, parent, false)!!
            }

            val nameTextView = convertView.order_menu_name as TextView
            nameTextView.text = mOrderList[position - 1].menu.name

            val unitTextView = convertView.order_menu_unit as TextView
            unitTextView.text = mOrderList[position - 1].unit.toString()

            val priceTextView = convertView.order_menu_price as TextView
            priceTextView.text = formatPriceString(mOrderList[position - 1].menu.price * mOrderList[position - 1].unit)

            val cancelButton = convertView.order_cancel as Button
            cancelButton.setOnClickListener { onClickCancel?.invoke(mOrderList[position - 1]) }

        }

        return convertView
    }

    fun setOrderList(orderList: ArrayList<Order>) {
        mOrderList = orderList
    }

    private fun formatPriceString(price :Int) :String {
        var format = "%,3d"
        return "￥" + format.format(price)
    }
}