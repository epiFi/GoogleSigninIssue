package com.epifi.paisa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class GoogleSignInTestActivity : AppCompatActivity(R.layout.activity_google_signin_test) {

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.plant(Timber.DebugTree())
        Timber.d("Started GoogleSignInTestActivity")
        super.onCreate(savedInstanceState)
        findViewById<ComposeView>(R.id.activity_composeview).setContent {
            GoogleSignInTestScreen(
                onClickSignin = { onClickSignin() },
                onClickSilentSignin = { onClickSilentSignin() },
                onClickSignout = { onClickSignout() },
                onClickRevokeAccess = { onClickRevokeAccess() },
            )
        }
    }

    private fun onClickSilentSignin() {
        lifecycleScope.launch {
            Timber.i("silenSignin started")
            val result = getClient()
                .silentSignIn()
                .addOnCompleteListener {
                    Timber.i("SilentSignin completed succesfully = ${it.isSuccessful}")
                    Timber.i("SilentSignin Exception ${it.exception?.message}")
                }
                .await() ?: throw NullPointerException("Silent sign in returned null")

            Timber.i(
                "Received isExpired = ${result.isExpired}, idToken =  ${result.idToken} "
            )
        }
    }

    private fun getClient(): GoogleSignInClient {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("432746002179-4q86u70ot3opldm79u9qnd82icd3b64h.apps.googleusercontent.com")
            .requestEmail()
            .requestProfile()
            .build()
            .let { gso -> GoogleSignIn.getClient(this@GoogleSignInTestActivity, gso) }
    }

    private fun onClickSignout() {
        lifecycleScope.launch {
            Timber.i("Signout started")
            getClient().signOut().addOnCompleteListener {
                Timber.i("Signout completed succesfully = ${it.isSuccessful}")
                Timber.i("Signout Exception ${it.exception?.message}")
            }.await()
        }
    }

    private fun onClickRevokeAccess() {
        Timber.i("revoke started")
        lifecycleScope.launch {
            val result = getClient().revokeAccess().addOnCompleteListener {
                Timber.i("revokeAccess completed succesfully = ${it.isSuccessful}")
                Timber.i("revokeAccess Exception ${it.exception?.message}")
            }.await()
        }
    }

    private fun onClickSignin() {
        Timber.i("Started a new signin flow")
        startActivityForResult(getClient().signInIntent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            Timber.i("Received sign in Result: resultCode: $resultCode isSuccessful = ${resultCode == Activity.RESULT_OK}")
            lifecycleScope.launch {
                try {
                    val result = GoogleSignIn.getSignedInAccountFromIntent(data).await()
                    Timber.i("Received token from signin ${result.idToken}")
                } catch (e: ApiException) {
                    Timber.e(e, "Sign in returned result")
                }
            }
        }
    }
}

@Composable
private fun GoogleSignInTestScreen(
    onClickSignin: () -> Unit,
    onClickSilentSignin: () -> Unit,
    onClickSignout: () -> Unit,
    onClickRevokeAccess: () -> Unit
) {
    Column {
        TextButton(onClick = onClickSignin) {
            Text(text = "Signin")
        }
        TextButton(onClick = onClickSilentSignin) {
            Text(text = "SilentSignin")
        }
        TextButton(onClick = onClickSignout) {
            Text(text = "Signout")
        }
        TextButton(onClick = onClickRevokeAccess) {
            Text(text = "revokeaccess")
        }
    }
}

@Preview
@Composable
private fun PreviewGoogleSignInTestScreen() {
    GoogleSignInTestScreen({}, {}, {}, {})
}
