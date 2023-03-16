package jp.co.ods.orderapp

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mCategoryList: ArrayList<Category>
    private lateinit var mAdapter: CategoryListAdapter

    private var mCategoryRef: DatabaseReference? = null

    private val mEventListener = object :ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val map = snapshot.value as Map<String, Any>
            val key = snapshot.key ?: ""
            val categoryName = map["category"].toString()

            val mMenuList = ArrayList<jp.co.ods.orderapp.Menu>()
            val menuMap = map["menu"] as Map<String, Any>?
            if (menuMap != null) {
                for (mapKey in menuMap.keys) {
                    val temp = menuMap[mapKey] as Map<String, String>
                    val menuName = temp["name"] ?: ""
                    val menuPrice = temp["price"].toString()
                    val menuImage = temp["image"] ?: ""
                    val menuExplain = temp["explain"] ?: ""
                    val menu = Menu(mapKey, menuName, menuPrice.toInt(), menuImage, menuExplain)
                    mMenuList.add(menu)
                }
            }

            val category = Category(key, categoryName, mMenuList)
            mCategoryList.add(category)

            //アダプターへ通知
            mAdapter.notifyDataSetChanged()
        }
        override fun onCancelled(error: DatabaseError) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val map = snapshot.value as Map<String, Any>
            val key = snapshot.key ?: ""
            val categoryName = map["category"].toString()

            for (i in 0..mCategoryList.size-1) {
                if (key == mCategoryList[i].key) {
                    mCategoryList[i].categoryName = categoryName
                }
            }
            //アダプターへ通知
            mAdapter.notifyDataSetChanged()
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val map = snapshot.value as Map<String, Any>
            val key = snapshot.key ?: ""
            val categoryName = map["category"].toString()

            val mMenuList = ArrayList<jp.co.ods.orderapp.Menu>()
            val menuMap = map["menu"] as Map<String, Any>?
            if (menuMap != null) {
                for (mapKey in menuMap.keys) {
                    val temp = menuMap[mapKey] as Map<String, String>
                    val menuName = temp["name"] ?: ""
                    val menuPrice = temp["price"].toString()
                    val menuImage = temp["image"] ?: ""
                    val menuExplain = temp["explain"] ?: ""
                    val menu = Menu(mapKey, menuName, menuPrice.toInt(), menuImage, menuExplain)
                    mMenuList.add(menu)
                }
            }

            val category = Category(key, categoryName, mMenuList)
            mCategoryList.remove(category)
            //アダプターへ通知
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // idがtoolbarがインポート宣言により取得されているので
        // id名でActionBarのサポートを依頼
        setSupportActionBar(toolbar)

        // fabにClickリスナーを登録
        fab.setOnClickListener { _ ->
            //注文リストへとばす
            val intent = Intent(applicationContext, OrderListActivity::class.java)
            startActivity(intent)
        }

        //Firebase用
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        //ListViewの準備
        mAdapter = CategoryListAdapter(this)
        mCategoryList = ArrayList<Category>()
        mAdapter.notifyDataSetChanged()

        listView.setOnItemClickListener {parent, view, position, id ->
            if (mCategoryRef != null) {
                mCategoryRef!!.removeEventListener(mEventListener)
            }

            //Categoryを渡してメニュー一覧画面を起動する
            val intent = Intent(applicationContext, MenuListActivity::class.java)
            intent.putExtra("category", mCategoryList[position])
            startActivity(intent)
        }


    }

    override fun onResume() {
        super.onResume()

        //preferenceから表示名を取得してタイトルに反映させる
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val storeName = sp.getString(StoreNameKEY, "")

        //UI設定
        title = storeName

        // ログイン済みのユーザーを取得する
        val user = FirebaseAuth.getInstance().currentUser
        // ログインしていなければログイン画面に遷移させる
        if (user == null) {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        } else {
            mCategoryList.clear()
            mAdapter.setCategoryList(mCategoryList)
            listView.adapter = mAdapter

            //ログインしている店の階層にリスナー登録
            if (mCategoryRef != null) {
                mCategoryRef!!.removeEventListener(mEventListener)
            }
            mCategoryRef = mDatabaseReference.child(user.uid).child(CategoryPath)
            mCategoryRef!!.addChildEventListener(mEventListener)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            val intent = Intent(applicationContext, SettingActivity::class.java)
            startActivity(intent)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}
