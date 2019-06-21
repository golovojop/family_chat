package k.s.yarlykov.familychat.ui

import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.SimpleAdapter
import k.s.yarlykov.familychat.R

class CustomViewBinder : SimpleAdapter.ViewBinder {

    override fun setViewValue(view: View, data: Any?, textRepresentation: String?): Boolean {

        val context = view.context

        when(view.id) {
            R.id.cvHistory -> {

                val drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    context.resources.getDrawable(view.id, context.theme)
                } else {
                    context.resources.getDrawable(view.id)
                }

                view.background = (drawable)
                return true
            }
        }

        return false
    }
}