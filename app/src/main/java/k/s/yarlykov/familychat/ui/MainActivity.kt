package k.s.yarlykov.familychat.ui

/**
 * https://code.tutsplus.com/ru/tutorials/how-to-create-an-android-chat-app-using-firebase--cms-27397
 * https://code.tutsplus.com/tutorials/how-to-create-an-android-chat-app-using-firebase--cms-27397?ec_unit=translation-info-language
 */

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseListAdapter
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import k.s.yarlykov.familychat.R
import k.s.yarlykov.familychat.ui.data.ChatMessage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.message.*
import java.text.DateFormat

private const val NONSECURE_NOTES = "nonsecure_notes"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        initViews()
        checkAuth()
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
                displayChatMessages()

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
            displayChatMessages()
            return
        }

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .build(),
            SIGN_IN_REQUEST_CODE
        )

//        if(FirebaseAuth.getInstance().currentUser == null) {
//            // Start sign in/sign up activity
//            startActivityForResult(
//                AuthUI.getInstance()
//                    .createSignInIntentBuilder()
//                    .build(),
//                SIGN_IN_REQUEST_CODE
//            )
//        } else {
//            // User is already signed in. Therefore, display
//            // a welcome Toast
//            Toast.makeText(this,
//                "Welcome " + FirebaseAuth.getInstance()
//                    .currentUser!!
//                    .displayName,
//                Toast.LENGTH_LONG)
//                .show()
//
//            // Load chat room contents
//            displayChatMessages()
//        }
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



    companion object {
        const val SIGN_IN_REQUEST_CODE = 101
    }
}
