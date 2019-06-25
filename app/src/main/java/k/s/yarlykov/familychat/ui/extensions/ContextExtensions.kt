package k.s.yarlykov.familychat.ui.extensions

import android.content.Context
import android.util.TypedValue

fun Context.dipToPix(dipValue: Float) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        dipValue,
        resources.displayMetrics).toInt()