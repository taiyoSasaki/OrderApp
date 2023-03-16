package jp.co.ods.orderapp


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_menu_add.*
import kotlinx.android.synthetic.main.list_menu.menu_name

class MenuAddActivity : AppCompatActivity() {

    private lateinit var mCategoryKey: String
    private lateinit var mCategoryName: String
    private lateinit var mMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_add)

        //preferenceから表示名を取得してタイトルに反映させる
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val storeName = sp.getString(StoreNameKEY, "")

        //渡ってきたMenu型オブジェクトを保存する
        val extras = intent.extras
        mCategoryKey = extras!!.get("categoryKey") as String
        mCategoryName = extras.get("categoryName") as String
        mMenu = extras.get("menu") as Menu

        //UI設定
        title = "$storeName > $mCategoryName > ${mMenu.name}"

        menu_name.text = mMenu.name
        if (mMenu.imageString.isNotEmpty()) {
            val bytes = Base64.decode(mMenu.imageString, Base64.DEFAULT)
            val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).copy(Bitmap.Config.ARGB_8888, true)
            menu_image.setImageBitmap(image)
        }

        menu_price.text = formatPriceString(mMenu.price)
        menu_explain.text = mMenu.explain

        //商品の個数処理
        var unit = 1
        menu_unit.text = unit.toString()

        menu_unit_plus.setOnClickListener {
            unit +=1
            menu_unit.text = unit.toString()
        }
        menu_unit_minus.setOnClickListener {
            unit -=1
            if (unit < 1) {
                unit = 1
            }
            menu_unit.text = unit.toString()
        }

        menu_order.setOnClickListener {v ->
            //注文リストへ追加し、注文リストへ飛ばす
            val orderUnit = menu_unit.text.toString().toInt()
            val order = Order(mMenu, orderUnit)

            val myApp = OrderApp.getInstance()
            myApp.addOrder(order)

            val intent = Intent(applicationContext, OrderListActivity::class.java)
            startActivity(intent)
            finish()
        }

        menu_return.setOnClickListener { v ->
            //メニュー表へ飛ばす
            finish()
        }
    }

    private fun formatPriceString(price :Int) :String {
        var format = "%,3d"
        return "￥" + format.format(price)
    }
}