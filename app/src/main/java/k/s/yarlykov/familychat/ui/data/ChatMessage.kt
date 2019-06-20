package k.s.yarlykov.familychat.ui.data

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class ChatMessage(val message: String? = "",
                       val user: String? = "",
                       val time: Long = Date().time)