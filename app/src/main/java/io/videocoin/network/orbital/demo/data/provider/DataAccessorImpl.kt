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

import android.app.Application
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import androidx.annotation.Keep
import androidx.annotation.NonNull
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.JsonObject
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.videocoin.android.app.demo.data.model.ProfilesList
import io.videocoin.android.app.demo.data.model.StreamData
import io.videocoin.network.orbital.demo.BuildConfig
import io.videocoin.network.orbital.demo.data.model.UserObj
import io.videocoin.network.orbital.demo.data.model.VideoData
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.Exception

class DataAccessorImpl(var app: Application) : DataAccessor {

    private val LOG_TAG: String = "DataAccessorImpl"

    private var apiService: ApiService? = null

    private var fireStoreInstance: FirebaseFirestore //firestore DB

    private var firebaseFileStorageInstance: FirebaseStorage //file storage (firebase storage)

    private var firebaseRemoteConfigInstance: FirebaseRemoteConfig

    private var userData: UserObj ?= null

    private val STUDIO_SANDBOX_URL = "https://studio.videocoin.network"
    private val AUTH_HEADER = "Authorization"
    private val PROFILES_URL = "/api/v1/profiles"
    private val STREAMS_URL = "/api/v1/streams"
    private val RUN_URL = "/run"
    private val STOP_URL = "/stop"

    private val STREAM_NAME_PROPERTY = "name"
    private val PROFILE_ID_PROPERTY = "profile_id"
    private val HEADER_BEARER = "Bearer "

    //firebase remote config keys
    private val REMOTE_CONFIG_API_KEY = "API_KEY"
    private val REMOTE_CONFIG_STREAM_PROFILE_ID_KEY = "ANDROID_STREAM_PROFILE_ID"
    private val REMOTE_CONFIG_LIVE_CAST_LIMIT_KEY = "DEFAULT_LIVE_CAST_TIME_LIMIT_IN_SEC"

    //firestore refs
    private val USERS_COLLECTION = "users"
    private val VIDEOS_COLLECTION = "videos"

    //firebase storage refs
    private val THUMBNAILS_STORAGE_REF = "video_thumbnails"

    //remote config values
    private var API_KEY : String ?= null
    private var DEFAULT_LIVE_CAST_TIME_LIMIT_IN_SEC : Long = 180
    private var STREAM_PROFILE_ID = "45d5ef05-efef-4606-6fa3-48f42d3f0b94"

    private val LIVE_CAST_ITEMS_LIMIT = 10
    private val RECENTLY_ENDED_ITEMS_LIMIT = 10

    init {
        apiService = createApiService()
        fireStoreInstance = FirebaseFirestore.getInstance()
        firebaseFileStorageInstance = FirebaseStorage.getInstance()
        firebaseRemoteConfigInstance = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfigInstance.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                Log.v(LOG_TAG, "remote config fetch succeeded")
                API_KEY = firebaseRemoteConfigInstance.getString(REMOTE_CONFIG_API_KEY)
                var timeLimit = firebaseRemoteConfigInstance.getLong(REMOTE_CONFIG_LIVE_CAST_LIMIT_KEY)
                if (timeLimit > 0) { //want to use value only if greater than 0
                    DEFAULT_LIVE_CAST_TIME_LIMIT_IN_SEC = timeLimit
                }
                var profileId = firebaseRemoteConfigInstance.getString(REMOTE_CONFIG_STREAM_PROFILE_ID_KEY)
                if (!TextUtils.isEmpty(profileId)) {
                    STREAM_PROFILE_ID = profileId
                }
            }
            else {
                Log.e(LOG_TAG, "remote config fetch failed")
            }
        }
    }

    private fun getServerUrl(): String {
        return STUDIO_SANDBOX_URL
    }

    override fun getStreamingProfiles(callback: Observer<ProfilesList>) {
        var streamingProfilesEndPoint: String = getServerUrl() + PROFILES_URL
        apiService?.getStreamingProfiles(streamingProfilesEndPoint)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe(callback)
    }

    override fun createStream(name: String, streamProfileId: String, callback: Observer<StreamData>) {
        var createStreamEndPoint: String = getServerUrl() + STREAMS_URL
        val obj = JsonObject()
        obj.addProperty(STREAM_NAME_PROPERTY, name)
        obj.addProperty(PROFILE_ID_PROPERTY, streamProfileId)
        apiService?.createStream(createStreamEndPoint, obj)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe(callback)
    }

    override fun prepareStream(streamId: String, callback: Observer<StreamData>) {
        var prepareStreamEndpoint: String = getServerUrl() + STREAMS_URL + "/" + streamId + RUN_URL
        apiService?.startStream(prepareStreamEndpoint)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe(callback)
    }

    override fun stopStream(streamId: String) {
        var stopStreamEndpoint: String = getServerUrl() + STREAMS_URL + "/" + streamId + STOP_URL
        apiService?.stopStream(stopStreamEndpoint)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe(StopStreamResultSubscriber())
    }

    override fun getStreamInfo(streamId: String, callback: Observer<StreamData>) {
        var getStreamEndpoint: String = getServerUrl() + STREAMS_URL + "/" + streamId
        apiService?.getStream(getStreamEndpoint)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe(callback)

    }

    override fun getLiveCasts(callback: Observer<ArrayList<VideoData>>) {
        var resultList = ArrayList<VideoData>()
        fireStoreInstance.collection(VIDEOS_COLLECTION)
                .whereEqualTo(VideoData.STATUS, VideoData.STATUS_LIVE)
                .orderBy(VideoData.CREATED_AT, Query.Direction.DESCENDING)
                .limit(LIVE_CAST_ITEMS_LIMIT.toLong())
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        resultList.add(document.toObject(VideoData::class.java)!!)
                    }
                    callback.onNext(resultList)
                }
                .addOnFailureListener { exception ->
                    Log.w(LOG_TAG, "Error getting documents: ", exception)
                    callback.onError(exception)
                }
    }

    override fun getRecentlyEnded(callback: Observer<ArrayList<VideoData>>) {
        var resultList = ArrayList<VideoData>()
        fireStoreInstance.collection(VIDEOS_COLLECTION)
                .whereEqualTo(VideoData.STATUS, VideoData.STATUS_ENDED)
                .orderBy(VideoData.CREATED_AT, Query.Direction.DESCENDING)
                .limit(RECENTLY_ENDED_ITEMS_LIMIT.toLong())
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        resultList.add(document.toObject(VideoData::class.java)!!)
                    }
                    callback.onNext(resultList)
                }
                .addOnFailureListener { exception ->
                    Log.w(LOG_TAG, "Error getting documents: ", exception)
                    callback.onError(exception)
                }
    }

    override fun storeUserInDB(userData: UserObj) {
        this.userData = userData
        var usersCollection = fireStoreInstance.collection(USERS_COLLECTION)
        usersCollection.document(userData.id!!).set(userData)
    }

    override fun syncUserFromDB(userObjParam: UserObj) {
        val docRef = fireStoreInstance.collection(USERS_COLLECTION).document(userObjParam.id!!)
        docRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot != null && documentSnapshot.data != null) {
                        this.userData = documentSnapshot.toObject(UserObj::class.java)!!
                    } else { //user not stored, so store
                        Log.v(LOG_TAG, "data is null, so attempt  to store user")
                        storeUserInDB(userObjParam)
                    }
                }
    }

    override fun getCurrentUser() : UserObj? {
        return userData
    }

    override fun setCurrentUser(userObj: UserObj) {
        userData = userObj
    }

    override fun getUsersLiveCastTimeLimit() : Int {
        var timeLimit = userData?.liveCastTimeLimit
        if (timeLimit == null || timeLimit == 0) {
            return DEFAULT_LIVE_CAST_TIME_LIMIT_IN_SEC as Int
        }
        else {
            return timeLimit
        }
    }

    override fun saveVideoDataToDB(obj: VideoData, callback: Observer<VideoData>) {
        fireStoreInstance.collection(VIDEOS_COLLECTION)
                .add(obj)
                .addOnSuccessListener { documentReference ->
                    Log.d(LOG_TAG, "video obj added with doc ref ID: ${documentReference.id}")
                    obj.id = documentReference.id
                    callback.onNext(obj)
                }
                .addOnFailureListener { e ->
                    Log.w(LOG_TAG, "Error saving video obj", e)
                }
    }

    override fun updateVideoObjInDB(obj: VideoData) {
        val docRef = fireStoreInstance.collection(VIDEOS_COLLECTION).document(obj.id!!)
        docRef.set(obj)
    }

    override fun uploadThumbnail(bitmap: Bitmap, fileName: String, callback: Observer<String>) {
        var imageStorageRef = firebaseFileStorageInstance.reference.child(THUMBNAILS_STORAGE_REF).child(fileName)
        var baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        var uploadTask = imageStorageRef.putBytes(baos.toByteArray())
        uploadTask.addOnFailureListener { callback.onError(Exception("upload failed"))}
                .addOnSuccessListener {
                    imageStorageRef.downloadUrl.addOnSuccessListener { uri -> callback.onNext(uri.toString()) }
                }
    }

    override fun getCacheDirectory() : File {
        return app.cacheDir
    }

    override fun getStreamProfileId(): String {
       return STREAM_PROFILE_ID
    }

    private inner class StopStreamResultSubscriber : CompletableObserver {
        override fun onComplete() {
            //stop was successful
        }

        override fun onSubscribe(d: Disposable) {

        }

        override fun onError(e: Throwable) {
            Log.e(LOG_TAG, "there was an error stopping the stream" + e?.message)
        }
    }


    private fun createApiService(): ApiService {

        val httpBuilder: OkHttpClient.Builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            httpBuilder.addInterceptor(loggingInterceptor)
        }

        httpBuilder.addInterceptor(HeaderInterceptor())

        val retrofit = Retrofit.Builder()
                .baseUrl(getServerUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpBuilder.build())
                .build()

        return retrofit.create(ApiService::class.java)
    }

    private inner class HeaderInterceptor : Interceptor {

        @Throws(IOException::class)
        override fun intercept(@NonNull chain: Interceptor.Chain): Response {
            val authHeader = chain.request().header(AUTH_HEADER)
            val authToken = API_KEY
            var newRequest = chain.request()
            if (!TextUtils.isEmpty(authHeader)) {
                newRequest = createRequestWithAuthHeader(chain.request())
            } else {
                if (!TextUtils.isEmpty(authToken)) {
                    newRequest = createRequestWithAuthHeader(chain.request())
                }
            }
            return chain.proceed(newRequest)
        }
    }

    @NonNull
    fun createRequestWithAuthHeader(@NonNull originalRequest: Request): Request {
        return originalRequest.newBuilder()
                .removeHeader(AUTH_HEADER)
                .addHeader(AUTH_HEADER, HEADER_BEARER + API_KEY)
                .build()
    }

    private interface ApiService {

        @Keep
        @GET
        fun getStreamingProfiles(@Url url: String): Observable<ProfilesList>

        @Keep
        @POST
        fun createStream(@Url url: String, @Body data: JsonObject): Observable<StreamData>

        @Keep
        @POST
        fun startStream(@Url url: String): Observable<StreamData>

        @Keep
        @POST
        fun stopStream(@Url url: String): Completable

        @Keep
        @GET
        fun getStream(@Url url: String): Observable<StreamData>
    }

}