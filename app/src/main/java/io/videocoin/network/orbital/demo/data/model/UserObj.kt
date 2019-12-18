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

package io.videocoin.network.orbital.demo.data.model

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.PropertyName

class UserObj {

    constructor()

    constructor(id: String, name: String, email: String, photoUrl: String) {
        this.id = id
        this.name = name
        this.email = email
        this.profileImgUrl = photoUrl
        liveCastTimeLimit = 0
    }

    constructor(firebaseUser: FirebaseUser) {
        id = firebaseUser.uid
        name = firebaseUser.displayName ?: "Unknown"
        email = firebaseUser.email ?: "UnknownEmail"
        profileImgUrl = firebaseUser.photoUrl?.toString() ?: ""
        liveCastTimeLimit = 0
    }

    @get:PropertyName("client")
    @set:PropertyName("client")
    var client = "android"
    @set:PropertyName("id")
    @get:PropertyName("id")
    var id : String ?= null
    @set:PropertyName("name")
    @get:PropertyName("name")
    var name: String? = null
    @get:PropertyName("email")
    @set:PropertyName("email")
    var email: String? = null
    @set:PropertyName("profile_img_url")
    @get:PropertyName("profile_img_url")
    var profileImgUrl: String? = null
    @set:PropertyName("livecast_time_limit")
    @get:PropertyName("livecast_time_limit")
    var liveCastTimeLimit: Int? = null


}