package k.s.yarlykov.familychat

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
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

    private fun checkAuth() {
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .build(),
                SIGN_IN_REQUEST_CODE
            )
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                "Welcome " + FirebaseAuth.getInstance()
                    .currentUser!!
                    .displayName,
                Toast.LENGTH_LONG)
                .show()

            // Load chat room contents
            displayChatMessages()
        }
    }

    private fun displayChatMessages() {

    }

    companion object {
        const val SIGN_IN_REQUEST_CODE = 101
    }
}
