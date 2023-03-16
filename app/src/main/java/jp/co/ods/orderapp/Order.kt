package jp.co.ods.orderapp

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Order (val menu: Menu, val unit: Int) {

    @Exclude
    fun toMap() :Map<String, Any> {
        return mapOf(
            "orderMenu" to menu.name,
            "orderUnit" to unit
        )
    }
}