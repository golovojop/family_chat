package k.s.yarlykov.familychat.ui

import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SimpleAdapter
import com.google.firebase.auth.FirebaseAuth
import k.s.yarlykov.familychat.R


import k.s.yarlykov.familychat.ui.extensions.dipToPix

class CustomViewBinder(val context: Context) : SimpleAdapter.ViewBinder {

    override fun setViewValue(view: View, data: Any?, textRepresentation: String?): Boolean {
        val context = view.context

        when (view.id) {
            R.id.llMessage -> {
                val layoutParams = view.layoutParams as LinearLayout.LayoutParams
                var border = R.drawable.message_border_left_angle

                if (FirebaseAuth.getInstance().currentUser!!.uid == (data as String)) {
                    border = R.drawable.message_border_right_angle
//                    layoutParams.marginStart = context.dipToPix(24F)
                    layoutParams.gravity = Gravity.END

                } else {
//                    layoutParams.marginEnd = context.dipToPix(24F)
                    layoutParams.gravity = Gravity.START
                }

                val drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    context.resources.getDrawable(border, context.theme)
                } else {
                    context.resources.getDrawable(border)
                }

                view.background = (drawable)
                view.layoutParams = layoutParams
                return true
            }
        }

        return false
    }

}