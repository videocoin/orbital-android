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

package io.videocoin.network.orbital.demo.data.provider

import android.graphics.Bitmap
import io.reactivex.Observer
import io.videocoin.android.app.demo.data.model.ProfilesList
import io.videocoin.android.app.demo.data.model.StreamData
import io.videocoin.network.orbital.demo.data.model.UserObj
import io.videocoin.network.orbital.demo.data.model.VideoData
import java.io.File

interface DataAccessor {

    fun getStreamingProfiles(callback: Observer<ProfilesList>)

    fun createStream(name: String, streamProfileId: String, callback: Observer<StreamData>)

    fun prepareStream(streamId: String, callback: Observer<StreamData>)

    fun stopStream(id: String)

    fun getStreamInfo(streamId: String, callback: Observer<StreamData>)

    fun getLiveCasts(callback: Observer<ArrayList<VideoData>>)

    fun getRecentlyEnded(callback: Observer<ArrayList<VideoData>>)

    fun storeUserInDB(userData: UserObj)

    fun syncUserFromDB(userObjParam: UserObj)

    fun setCurrentUser(userObj: UserObj)

    fun getCurrentUser() : UserObj?

    fun getUsersLiveCastTimeLimit() : Int

    fun saveVideoDataToDB(obj: VideoData, callback: Observer<VideoData>)

    fun updateVideoObjInDB(obj: VideoData)

    fun uploadThumbnail(obj: Bitmap, fileName: String, callback: Observer<String>)

    fun getCacheDirectory() : File

    fun getStreamProfileId(): String

}