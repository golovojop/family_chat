package k.s.yarlykov.familychat.ui.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.format(format: String) = SimpleDateFormat(format, Locale.getDefault()).format(this)