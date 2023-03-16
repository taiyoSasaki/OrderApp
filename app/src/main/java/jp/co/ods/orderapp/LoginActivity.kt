package jp.co.ods.orderapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.preference.PreferenceManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mLoginListener: OnCompleteListener<AuthResult>
    private lateinit var mDataBaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mDataBaseReference = FirebaseDatabase.getInstance().reference

        // FirebaseAuthのオブジェクトを取得する
        mAuth = FirebaseAuth.getInstance()

        // ログイン処理のリスナー
        mLoginListener = OnCompleteListener { task ->
            if (task.isSuccessful) {
                // 成功した場合
                val user = mAuth.currentUser
                val userRef = mDataBaseReference.child(StoresPATH).child(user!!.uid)

                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val data = snapshot.value as Map<*, *>?
                        saveName(data!!["StoreName"] as String)
                    }

                    override fun onCancelled(firebaseError: DatabaseError) {

                    }
                })

                // プログレスバーを非表示にする
                progressBar.visibility = View.GONE

                // Activityを閉じる
                finish()

            } else {
                // 失敗した場合
                // エラーを表示する
                val view = findViewById<View>(android.R.id.content)
                Snackbar.make(view, getString(R.string.login_failure_message), Snackbar.LENGTH_LONG).show()

                // プログレスバーを非表示にする
                progressBar.visibility = View.GONE
            }
        }

        // タイトルの設定
        title = getString(R.string.login_title)


        loginButton.setOnClickListener { v ->
            // キーボードが出てたら閉じる
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            if (email.length != 0 && password.length >= 6) {
                login(email, password, v)
            } else {
                // エラーを表示する
                Snackbar.make(v, getString(R.string.login_error_message), Snackbar.LENGTH_LONG).show()
            }
        }
    }


    private fun login(email: String, password: String, v: View) {
        //卓番号の入力
        // ダイアログ専用のレイアウトを読み込む
        val dialogLayout = LayoutInflater.from(this).inflate(R.layout.edit_text_dialog, null)
        val editText = dialogLayout.findViewById<AppCompatEditText>(R.id.editTextDialog)

        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.table_number_title))
            .setMessage(getString(R.string.table_number_message))
            .setView(dialogLayout)
            .setPositiveButton(getString(R.string.dialog_positive)) { _, _ ->
                // キーボードが出てたら閉じる
                val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                //OKボタンを押したとき
                val sp = PreferenceManager.getDefaultSharedPreferences(this)
                val editor = sp.edit()
                editor.putString(TableNumberKey, editText.text.toString())
                editor.commit()

                // プログレスバーを表示する
                progressBar.visibility = View.VISIBLE
                // ログインする
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(mLoginListener)
            }
            .setNegativeButton(getString(R.string.dialog_negative), null)
            .create()

        dialog.show()

        // ダイアログのボタンを取得し、デフォルトの色を設定
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false //デフォルトでオフにしておく
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.gray))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.hotPink))

        // AppCompatEditTextにTextChangedListenerをセット
        editText.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // 1~3文字の時だけOKボタンを有効化する
                if (s.isNullOrEmpty() || s.length > 3) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.gray))
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.hotPink))
                }
            }
        })
    }

    private fun saveName(name: String) {
        // Preferenceに保存する
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sp.edit()
        editor.putString(StoreNameKEY, name)
        editor.commit()
    }

}