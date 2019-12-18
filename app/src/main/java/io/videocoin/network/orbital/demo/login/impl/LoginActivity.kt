/*
 * MIT License
 *
 * Copyright (c) 2019 VideoCoin Network
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.videocoin.network.orbital.demo.login.impl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import io.videocoin.network.orbital.demo.R
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes.NETWORK_ERROR
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.videocoin.network.orbital.demo.data.provider.DataAccessorProvider
import io.videocoin.network.orbital.demo.home.impl.HomePageActivity
import io.videocoin.network.orbital.demo.login.LoginPresenter
import io.videocoin.network.orbital.demo.login.LoginView
import io.videocoin.network.orbital.demo.utils.ui.SnackBarHelper
import kotlinx.android.synthetic.main.login_with_google_view.*

class LoginActivity: AppCompatActivity(), LoginView {

    private val REQUEST_CODE_SIGNIN : Int = 5321
    private val TAG: String = "LoginActivity"
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    var snackbar: Snackbar ?= null
    var loginPresenter: LoginPresenter?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_with_google_view)
        loginPresenter = LoginPresenterImpl(this, application as DataAccessorProvider)
        auth = FirebaseAuth.getInstance()

        //configure google sign-in options and create googlesigninclient
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        login_with_google_button?.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, REQUEST_CODE_SIGNIN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.signInIntent
        if (requestCode == REQUEST_CODE_SIGNIN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        //else nothing to do; request code did not match
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            //signed in successfully, now proceed to authenticate with firebase for the acct
            firebaseAuthWithGoogle(account!!)
        } catch (exception: ApiException) {
            Log.w(TAG, "sign failed with code = " + exception.statusCode)
            showSignInError(exception.statusCode)
        }

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.v(TAG, "firebase auth success")
                        val user = auth.currentUser
                        if (user != null) {
                            loginPresenter?.saveUser(user!!)
                        }
                    } else {
                        Log.e(TAG, "firebase auth failure: " + task.exception)
                        showSnackbarMessage(task.exception?.localizedMessage ?: resources.getString(R.string.auth_error)) //try to get the localized error message from firebase and show, if null show default auth error message
                    }
                }
    }

    private fun showSignInError(code: Int) {
        when(code) {
            NETWORK_ERROR -> {
                showSnackbarMessage(resources.getString(R.string.check_internet))
            }
            else -> {
                showSnackbarMessage(resources.getString(R.string.auth_error))
            }
        }
    }

    private fun showSnackbarMessage(msg: String) {
        if (snackbar == null) {
            snackbar = SnackBarHelper.createSnackBar(this, snackbar_action!!, msg, null, null)
        }
        snackbar?.setText(msg)
        snackbar?.show()
    }

    private fun showHomePage() {
        val homePageIntent = Intent(this, HomePageActivity::class.java)
        startActivity(homePageIntent)
    }

    override fun onUserSaved() {
        showHomePage()
        finish()
    }

}