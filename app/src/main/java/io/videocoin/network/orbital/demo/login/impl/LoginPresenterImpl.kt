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

import com.google.firebase.auth.FirebaseUser
import io.videocoin.network.orbital.demo.data.provider.DataAccessorProvider
import io.videocoin.network.orbital.demo.data.model.UserObj
import io.videocoin.network.orbital.demo.data.provider.DataAccessor
import io.videocoin.network.orbital.demo.login.LoginPresenter
import io.videocoin.network.orbital.demo.login.LoginView

class LoginPresenterImpl(var loginView: LoginView, accessible: DataAccessorProvider) : LoginPresenter {

    private var dataAccessor: DataAccessor = accessible.getDataAccessor()

    override fun saveUser(user: FirebaseUser) {
        var userData = UserObj(user)
        dataAccessor.storeUserInDB(userData) //async save to db, but not listening for callback as there is no messaging to be shown to user
        loginView.onUserSaved()
    }
}