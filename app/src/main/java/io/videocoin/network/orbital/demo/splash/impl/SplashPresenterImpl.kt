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

package io.videocoin.network.orbital.demo.splash.impl

import com.google.firebase.auth.FirebaseAuth
import io.videocoin.network.orbital.demo.data.provider.DataAccessorProvider
import io.videocoin.network.orbital.demo.data.model.UserObj
import io.videocoin.network.orbital.demo.data.provider.DataAccessor
import io.videocoin.network.orbital.demo.splash.SplashPresenter
import io.videocoin.network.orbital.demo.splash.SplashView

class SplashPresenterImpl(view: SplashView, accessible: DataAccessorProvider) : SplashPresenter {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var splashView: SplashView = view
    var dataAccessor: DataAccessor = accessible.getDataAccessor()

    override fun checkLogin() {
        if (firebaseAuth.currentUser == null) {
            splashView.showLogin()
        }
        else {
            var user = firebaseAuth.currentUser
            var userObj = UserObj(user!!)
            dataAccessor.setCurrentUser(userObj) //user info updated so we can display user profile image in homepage
            dataAccessor.syncUserFromDB(userObj) //doing this so the livecast time limit can be read if it was modified for the user in the db. This is async, but don't need to wait for updated info - since stream time limit data will be used only during livecast
            splashView.showHome()
        }
    }
}