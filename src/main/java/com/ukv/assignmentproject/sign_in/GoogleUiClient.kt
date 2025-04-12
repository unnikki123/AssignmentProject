package com.ukv.assignmentproject.sign_in

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ukv.assignmentproject.R
import kotlinx.coroutines.tasks.await

class GoogleUiClient(
    private val context: Context
) {
    private val auth = Firebase.auth

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val user = auth.signInWithCredential(credential).await().user
            SignInResult(
                data = user?.run {
                    User(
                        userId = uid,
                        username = displayName,
                        profilePictureUrl = photoUrl?.toString()
                    )
                },
                errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            SignInResult(null, e.localizedMessage)
        }
    }

    fun signOut() {
        googleSignInClient.signOut()
        auth.signOut()
    }

    fun getSignedInUser(): User? = auth.currentUser?.run {
        User(
            userId = uid,
            username = displayName,
            profilePictureUrl = photoUrl?.toString()
        )
    }
}
