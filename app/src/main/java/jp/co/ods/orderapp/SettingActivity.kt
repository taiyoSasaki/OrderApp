package jp.co.ods.orderapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    private lateinit var mDataBaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        //preferenceから卓番号を取得してEditTextに反映させる
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val tableNumber = sp.getString(TableNumberKey, "")
        table_number.setText(tableNumber)

        mDataBaseReference = FirebaseDatabase.getInstance().reference

        //UIの初期化
        title = getString(R.string.settings_title)

        logoutButton.setOnClickListener { v ->
            //ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                //ログインしていない場合は何もしない
                Snackbar.make(v, getString(R.string.no_login_user), Snackbar.LENGTH_LONG).show()
            } else {
                FirebaseAuth.getInstance().signOut()  //ログアウト
                finish()
            }
        }

        change_number.setOnClickListener { v ->
            //キーボードが出ていたら閉じる
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            //変更した表示名をpreferenceに保存する
            val number2 = table_number.text.toString()
            val sp2 = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val editor = sp2.edit()
            editor.putString(TableNumberKey, number2)
            editor.commit()

            Snackbar.make(v, getString(R.string.change_disp_name), Snackbar.LENGTH_LONG).show()
        }
    }
}