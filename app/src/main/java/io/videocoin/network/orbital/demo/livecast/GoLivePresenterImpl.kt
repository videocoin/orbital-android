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

package io.videocoin.network.orbital.demo.livecast

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import com.github.faucamp.simplertmp.RtmpHandler
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.videocoin.android.app.demo.data.model.ProfilesList
import io.videocoin.android.app.demo.data.model.StreamData
import io.videocoin.network.orbital.demo.data.provider.DataAccessorProvider
import io.videocoin.network.orbital.demo.data.model.VideoData
import io.videocoin.network.orbital.demo.data.provider.DataAccessor
import net.ossrs.yasea.SrsCameraView
import net.ossrs.yasea.SrsEncodeHandler
import net.ossrs.yasea.SrsPublisher
import net.ossrs.yasea.SrsRecordHandler
import java.io.File
import java.io.IOException
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.net.SocketException

class GoLivePresenterImpl(val goLiveView: GoLiveView, accessible: DataAccessorProvider) : GoLivePresenter, RtmpHandler.RtmpListener, SrsEncodeHandler.SrsEncodeListener, SrsRecordHandler.SrsRecordListener {

    private var dataAccessor: DataAccessor = accessible.getDataAccessor()
    private var LOG_TAG : String = "GoLivePresenterImpl"
    private var disposables: CompositeDisposable = CompositeDisposable()
    private lateinit var title: String
    private var handler = Handler()
    private var streamStatusPollTask : StreamStatusPollTask ?= null
    private var STREAM_STATUS_POLL_INTERVAL = 3000
    private var cameraDataPublisher: SrsPublisher? = null
    private var STREAM_PROFILE_ID : String
    private var currentStreamId: String? = null
    private var outputUrl: String ?= null
    private var currentVideoData: VideoData ?= null
    private var isVideoDataRecorded: Boolean = false
    private var recordingForthumbnailPath : String ?= null
    private var videoImgThumbnail : String?= null
    private var DEFAULT_VIDEO_ID = "id"
    private var isRtmpStreamingStarted = false

    init {
        recordingForthumbnailPath = dataAccessor.getCacheDirectory().path + "/clip.mp4"
        STREAM_PROFILE_ID = dataAccessor.getStreamProfileId()
    }

    override fun goLive(title: String) {
        this.title = title
        isVideoDataRecorded = false
        goLiveView.showGoingLiveProgress()
        dataAccessor.getStreamingProfiles(StreamingProfilesResultSubscriber())
    }

    override fun cancelGoLive() {

    }

    override fun setupCameraDataPublisher(cameraView: SrsCameraView) {
        cameraDataPublisher = SrsPublisher(cameraView)
        cameraDataPublisher?.setEncodeHandler(SrsEncodeHandler(this))
        cameraDataPublisher?.setRecordHandler(SrsRecordHandler(this))
        cameraDataPublisher?.setRtmpHandler(RtmpHandler(this))
        cameraDataPublisher?.setPreviewResolution(640, 360)
        cameraDataPublisher?.setOutputResolution(360, 640)
        cameraDataPublisher?.setVideoHDMode()
    }

    override fun endLiveCast() {
        doEndLiveCastCleanup()
    }

    private fun doEndLiveCastCleanup() {
        handler.removeCallbacks(streamStatusPollTask)
        if (isRtmpStreamingStarted) {
            cameraDataPublisher?.stopPublish()
            isRtmpStreamingStarted = false
        }
        cameraDataPublisher?.stopCamera()
        if (currentStreamId != null) {
            dataAccessor.stopStream(currentStreamId!!)
        }
        if (currentVideoData != null) {
            currentVideoData?.status = VideoData.STATUS_ENDED
            dataAccessor.updateVideoObjInDB(currentVideoData!!)
            goLiveView.showLiveCastEndedView(currentVideoData)
        }
    }

    private inner class StreamingProfilesResultSubscriber : Observer<ProfilesList> {
        override fun onComplete() {

        }

        override fun onSubscribe(d: Disposable) {
            disposables.add(d)
        }

        override fun onNext(profilesList: ProfilesList) {
            if (profilesList.items != null) {
                for (profile in profilesList.items!!) {
                    if (profile.id == STREAM_PROFILE_ID) {
                        dataAccessor?.createStream(title, STREAM_PROFILE_ID, CreateStreamResultSubscriber())
                        return
                    }
                }
            }
            goLiveView.showErrorCreatingLiveCast() //unable to find the matching profile id for streaming from mobile, show error
            goLiveView.showGoLiveView()
        }

        override fun onError(e: Throwable) {
            goLiveView.showErrorCreatingLiveCast()
            goLiveView.showGoLiveView()
        }

    }

    private inner class CreateStreamResultSubscriber : Observer<StreamData> {
        override fun onComplete() {

        }

        override fun onSubscribe(d: Disposable) {
            disposables.add(d)
        }

        override fun onNext(streamData: StreamData) {
            currentStreamId = streamData.id
            outputUrl = streamData.outputUrl
            dataAccessor?.prepareStream(streamData.id!!, StartStreamResultSubscriber())
        }

        override fun onError(e: Throwable) {
            goLiveView.showErrorCreatingLiveCast()
            goLiveView.showGoLiveView()
        }

    }

    private inner class StartStreamResultSubscriber : Observer<StreamData> {
        override fun onComplete() {

        }

        override fun onSubscribe(d: Disposable) {
            disposables.add(d)
        }

        override fun onNext(streamData: StreamData) {
            streamStatusPollTask = StreamStatusPollTask(streamData.id!!, STREAM_STATUS_POLL_INTERVAL) //start polling for the stream status
            handler.post(streamStatusPollTask)
        }

        override fun onError(e: Throwable) {
            goLiveView.showErrorCreatingLiveCast()
            goLiveView.showGoLiveView()
        }

    }

    private inner class GetStreamResultSubscriber : Observer<StreamData> {
        override fun onComplete() {

        }

        override fun onSubscribe(d: Disposable) {
            disposables.add(d)
        }

        override fun onNext(streamData: StreamData) {
            when {
                StreamData.STREAM_STATUS_FAILED == streamData.status || StreamData.STREAM_STATUS_CANCELLED == streamData.status -> {
                    handler.removeCallbacks(streamStatusPollTask)
                    goLiveView.showErrorCreatingLiveCast()
                    goLiveView.showGoLiveView()
                }
                StreamData.STREAM_STATUS_COMPLETED == streamData.status -> { //handle case when stream is stopped from outside the io.videocoin.network.orbital.demo
                    doEndLiveCastCleanup()
                }
                StreamData.STREAM_STATUS_PREPARED == streamData.status -> { //stream status is prepared. Now can start publishing to rtmp ingest point
                    cameraDataPublisher?.startPublish(streamData.rtmpUrl)
                    cameraDataPublisher?.startCamera()
                    isRtmpStreamingStarted = true
                }
                StreamData.STREAM_STATUS_READY == streamData.status -> { //stream is ready. The output can be viewed
                    if (!isVideoDataRecorded) {
                        currentVideoData = VideoData(DEFAULT_VIDEO_ID, title, videoImgThumbnail, System.currentTimeMillis(), 0, VideoData.STATUS_LIVE, outputUrl, dataAccessor.getCurrentUser()?.name, dataAccessor.getCurrentUser()?.profileImgUrl)
                        dataAccessor.saveVideoDataToDB(currentVideoData!!, UpdatedVideoDataSubscriber())
                        goLiveView.showLiveNowView()
                        isVideoDataRecorded = true
                    }
                }
                else -> Log.v(LOG_TAG, "stream is not ready. status is = " + streamData.status)
            }
        }

        override fun onError(e: Throwable) {
            goLiveView.showErrorCreatingLiveCast()
        }
    }

    private inner class UpdatedVideoDataSubscriber: Observer<VideoData> {
        override fun onComplete() {

        }

        override fun onSubscribe(d: Disposable) {
            disposables.add(d)
        }

        override fun onNext(data: VideoData) {
            currentVideoData = data
        }

        override fun onError(e: Throwable) {
            //hmm, shouldn't happen. failed to write to db
        }

    }

    private inner class ImageUploadSubscriber : Observer<String> {
        override fun onComplete() {

        }

        override fun onSubscribe(d: Disposable) {
            disposables.add(d)
        }

        override fun onNext(imgUrl: String) {
            currentVideoData?.imageUrl = imgUrl
            videoImgThumbnail = imgUrl
            deleteTmpFile()
        }

        override fun onError(e: Throwable) {
           deleteTmpFile()
        }

        private fun deleteTmpFile() {
            try {
                File(recordingForthumbnailPath).delete()
            } catch (e : Exception) {
                //it's ok to ignore exception, this is in app's cache storage which os will reclaim if required
            }
        }
    }

    override fun onResume() {
        cameraDataPublisher?.startCamera()
    }


    override fun onPause() {
        disposables.clear()
        if (streamStatusPollTask != null) {
            handler.removeCallbacks(streamStatusPollTask)
        }
        doEndLiveCastCleanup()
    }

    private inner class StreamStatusPollTask(var streamId: String, var pollInterval: Int) : Runnable {

        override fun run() {
            dataAccessor.getStreamInfo(streamId, GetStreamResultSubscriber())
            handler.postDelayed(this, pollInterval.toLong())
        }
    }

    /**
     * RtmpListener methods
     */
    override fun onRtmpConnecting(msg: String?) {

    }

    override fun onRtmpConnected(msg: String?) {
        /**
         * capture 2 sec of video for video thumbnail extraction
         */
        cameraDataPublisher?.startRecord(recordingForthumbnailPath)
        handler.postDelayed( { cameraDataPublisher?.stopRecord() }, 2000)
    }

    override fun onRtmpVideoStreaming() {

    }

    override fun onRtmpAudioStreaming() {

    }

    override fun onRtmpStopped() {

    }

    override fun onRtmpDisconnected() {

    }

    override fun onRtmpVideoFpsChanged(fps: Double) {

    }

    override fun onRtmpVideoBitrateChanged(bitrate: Double) {

    }

    override fun onRtmpAudioBitrateChanged(bitrate: Double) {

    }

    override fun onRtmpSocketException(e: SocketException?) {

    }

    override fun onRtmpIOException(e: IOException?) {

    }

    override fun onRtmpIllegalArgumentException(e: IllegalArgumentException?) {
    }

    override fun onRtmpIllegalStateException(e: IllegalStateException?) {

    }

    /**
     * SrsEncodeListener methods
     */
    override fun onNetworkWeak() {
        goLiveView.showErrorCreatingLiveCast() //TODO: more informative error
        goLiveView.showGoLiveView()
    }

    override fun onNetworkResume() {

    }

    override fun onEncodeIllegalArgumentException(e: IllegalArgumentException?) {

    }

    /**
     * SrsRecordListener methods
     */
    override fun onRecordPause() {

    }

    override fun onRecordResume() {

    }

    override fun onRecordStarted(msg: String?) {

    }

    override fun onRecordFinished(msg: String?) {
        /**
         * recording completed, now can extract thumbnail
         */
        var bitmap: Bitmap? = ThumbnailUtils.createVideoThumbnail(recordingForthumbnailPath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND)
        if (bitmap != null) {
            var fileName = "image_$currentStreamId" + "_" + System.currentTimeMillis() //create file name with stream id
            dataAccessor.uploadThumbnail(bitmap, fileName, ImageUploadSubscriber())
        }
        else {
            Log.e(LOG_TAG, "error: thumbnail was not generated")
        }
    }

    override fun onRecordIllegalArgumentException(e: IllegalArgumentException?) {

    }

    override fun onRecordIOException(e: IOException?) {

    }
}