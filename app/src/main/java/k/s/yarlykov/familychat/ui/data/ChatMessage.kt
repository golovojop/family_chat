package k.s.yarlykov.familychat.ui.data

import java.util.*

data class ChatMessage(val message: String,
                       val user: String,
                       val time: Long = Date().time)