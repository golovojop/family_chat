package k.s.yarlykov.familychat.ui

/**
 * https://code.tutsplus.com/ru/tutorials/how-to-create-an-android-chat-app-using-firebase--cms-27397
 * https://code.tutsplus.com/tutorials/how-to-create-an-android-chat-app-using-firebase--cms-27397?ec_unit=translation-info-language
 */

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseListAdapter
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import k.s.yarlykov.familychat.R
import k.s.yarlykov.familychat.ui.data.ChatMessage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.message.*
import java.text.DateFormat
import java.util.*

private const val NONSECURE_NOTES = "nonsecure_notes"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        initViews()
        checkAuth()
        renderChatMessages()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId) {
            R.id.menu_sign_out -> {
                AuthUI
                    .getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        Toast.makeText(this@MainActivity,
                            "You have been signed out.",
                            Toast.LENGTH_LONG)
                            .show()
                        finish()
                    }
            }
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                Toast.makeText(this,
                    "Successfully signed in. Welcome!",
                    Toast.LENGTH_LONG)
                    .show()
                renderChatMessages()

            } else {
                Toast.makeText(this,
                    "We couldn't sign you in. Please try again later.",
                    Toast.LENGTH_LONG)
                    .show()
                finish()
            }
        }
    }

    fun initViews() {
        fab.setOnClickListener {

            val refRoot = FirebaseDatabase
                .getInstance()
                .reference
            val refNotes = refRoot.child(NONSECURE_NOTES)

            refNotes
                .push()
                .setValue(ChatMessage(
                    input.text.toString(),
                    FirebaseAuth.getInstance().currentUser!!.displayName!!))
            input.setText("")
        }
    }

    private fun checkAuth() {
        FirebaseAuth.getInstance().currentUser?.let {
            renderChatMessages()
            return
        }

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .build(),
            SIGN_IN_REQUEST_CODE
        )

    }

    private fun displayChatMessages() {

        /**
         * Вот пример того как правильно запрашивать данные.
         * Как проверять, что подключен к базе и т.д.
         * https://github.com/googlearchive/AndroidChat/blob/master/app/src/main/java/com/firebase/androidchat/MainActivity.java
         */

        val adapter = object: FirebaseListAdapter<ChatMessage>(
            this,
            ChatMessage::class.java,
            R.layout.message,
            FirebaseDatabase.getInstance().reference.child(NONSECURE_NOTES)) {

            override fun populateView(v: View?, model: ChatMessage?, position: Int) {
                messageText.text = model?.message
                messageUser.text = model?.user
                messageTime.text = DateFormat.getDateInstance().format(model?.time)
            }
        }

        list_of_messages.adapter = adapter

    }

    private fun renderChatMessages() {
        FirebaseDatabase.getInstance().reference.child(NONSECURE_NOTES)
        .addValueEventListener (
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val list = mutableListOf<ChatMessage>()

                    for(child in snapshot.children) {
                        list.add(child.getValue(ChatMessage::class.java)!!)
                    }

                    if(list.size > 0) {
                        initListView(list_of_messages, list)
                    } else {
                        val snackbar = Snackbar.make(
                            list_of_messages,
                            "Can't receive data from Cloud",
                            Snackbar.LENGTH_LONG)
                            .setAction("OK", null)
                        snackbar.duration = 3000
                        snackbar.show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, "Operation canceled", Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    private fun initListView(lv: ListView, messages: List<ChatMessage>) {
        val data = ArrayList<Map<String, Any?>>()
        for (message in messages) {
            val map = mapOf(
                KEY_MESSAGE to message.message,
                KEY_USER to message.user,
                KEY_TIME to android.text.format.DateFormat.format(timeFormat(message.time), Date(message.time)))
            data.add(map)
        }

        val from = arrayOf(KEY_MESSAGE, KEY_USER, KEY_TIME)
        val to = intArrayOf(R.id.messageText, R.id.messageUser, R.id.messageTime)

        lv.adapter = SimpleAdapter(
            this,
            data,
            R.layout.message,
            from,
            to).apply {
            viewBinder = CustomViewBinder()
        }
    }


    // Определаить в каком формате выводить время сообщения
    private fun timeFormat(time: Long) : String {
        val c = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val currentDayDuration = Date().time - c.timeInMillis
        return if(Date().time - time > currentDayDuration) "dd/MM HH:mm" else "HH:mm"
    }

    companion object {
        const val SIGN_IN_REQUEST_CODE = 101

        const val KEY_MESSAGE = "message"
        const val KEY_USER = "user"
        const val KEY_TIME = "time"
    }
}
