package jp.co.ods.orderapp

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Category(val key: String, var categoryName: String, val menuList: ArrayList<Menu>) :Serializable {

}