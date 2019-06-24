package k.s.yarlykov.familychat.ui

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SimpleAdapter
import com.google.firebase.auth.FirebaseAuth
import k.s.yarlykov.familychat.R

class CustomViewBinder : SimpleAdapter.ViewBinder {

    override fun setViewValue(view: View, data: Any?, textRepresentation: String?): Boolean {
        val context = view.context

        when(view.id) {
            R.id.llMessage -> {

                val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams

                var border = R.drawable.message_border_left_angle
//                layoutParams.marginStart = 0
//                layoutParams.marginEnd = 20

                if(FirebaseAuth.getInstance().currentUser!!.uid == (data as String)) {
                    border = R.drawable.message_border_right_angle
//                    layoutParams.marginStart = 20
//                    layoutParams.marginEnd = 0
                }

                val drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    context.resources.getDrawable(border, context.theme)
                } else {
                    context.resources.getDrawable(border)
                }

                view.background = (drawable)
//                view.layoutParams = layoutParams
                return true
            }
        }

        return false
    }
}