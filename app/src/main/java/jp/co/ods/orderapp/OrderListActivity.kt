package jp.co.ods.orderapp

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_order_list.*

class OrderListActivity : AppCompatActivity() {

    private lateinit var mAdapter: OrderListAdapter
    private lateinit var mOrderList: ArrayList<Order>
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var tableNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)

        //Firebase用
        var user = FirebaseAuth.getInstance().currentUser
        mDatabaseReference = FirebaseDatabase.getInstance().reference
        var mOrderRef = mDatabaseReference.child(user!!.uid).child(OrderPath)

        //preferenceから卓番号を取得する
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        tableNumber = sp.getString(TableNumberKey, "").toString()

        val myApp = OrderApp.getInstance()
        mOrderList = myApp.getOrderList()

        //ListViewの準備
        mAdapter = OrderListAdapter(this)
        mAdapter.apply {
            onClickCancel = {
                //メニュー削除メソッド
                //ダイアログを表示する
                val builder = AlertDialog.Builder(this@OrderListActivity)
                    .setTitle(getString(R.string.menu_remove_title))
                    .setMessage(getString(R.string.menu_remove_message))
                    .setPositiveButton(getString(R.string.menu_remove)) { _, _ ->
                        val myApp = OrderApp.getInstance()
                        myApp.removeOrder(it)
                        mOrderList = myApp.getOrderList()
                        mAdapter.notifyDataSetChanged()
                    }
                    .setNegativeButton(getString(R.string.menu_cancel_remove), null)
                builder.create()
                builder.show()
            }
        }
        mAdapter.setOrderList(mOrderList)

        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        order_button.setOnClickListener {
            mOrderList = myApp.getOrderList()
            var sendOrder = ArrayList<Map<String, Any>>()
            for (order in mOrderList) {
                sendOrder.add(order.toMap())
            }

            val data = mapOf<String, Any>("TableNumber" to tableNumber, "OrderList" to sendOrder)
            mOrderRef.push().setValue(data)
                .addOnSuccessListener {
                    //注文リストを注文済みリストへ
                    myApp.clearOrderList()
                    mOrderList = myApp.getOrderList()
                    mAdapter.notifyDataSetChanged()
                    finish()
                }
        }

        order_return.setOnClickListener {
            finish()
        }

    }

}