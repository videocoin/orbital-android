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

import com.google.firebase.firestore.PropertyName

class VideoData {

    companion object {
        const val CREATED_AT = "created_at"
        const val STATUS = "status"

        const val STATUS_ENDED = "ENDED"
        const val STATUS_LIVE = "LIVE"
    }

    constructor()
    constructor(id: String?, title: String?, imageUrl: String?, createdAt: Long?, duration: Int?, status: String?, playbackUrl: String?, creatorName: String?, creatorPhotoUrl: String?) {
        this.id = id
        this.title = title
        this.imageUrl = imageUrl
        this.createdAt = createdAt
        this.duration = duration
        this.status = status
        this.playbackUrl = playbackUrl
        this.creatorName = creatorName
        this.creatorPhotoUrl = creatorPhotoUrl
    }

    @set:PropertyName("client")
    @get:PropertyName("client")
    var client = "android"
    @set:PropertyName("id")
    @get:PropertyName("id")
    var id : String ?= null
    @set:PropertyName("title")
    @get:PropertyName("title")
    var title: String? = null
    @set:PropertyName("image_url")
    @get:PropertyName("image_url")
    var imageUrl: String? = null
    @set:PropertyName(CREATED_AT)
    @get:PropertyName(CREATED_AT)
    var createdAt: Long? = null
    @set:PropertyName("duration")
    @get:PropertyName("duration")
    var duration: Int? = null
    @set:PropertyName(STATUS)
    @get:PropertyName(STATUS)
    var status: String ?= null
    @set:PropertyName("playback_url")
    @get:PropertyName("playback_url")
    var playbackUrl: String ?= null
    @set:PropertyName("creator_name")
    @get:PropertyName("creator_name")
    var creatorName: String? = null
    @set:PropertyName("creator_img_url")
    @get:PropertyName("creator_img_url")
    var creatorPhotoUrl: String? = null

}