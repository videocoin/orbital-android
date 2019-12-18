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

package io.videocoin.network.orbital.demo.playback

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import io.videocoin.network.orbital.demo.R
import kotlinx.android.synthetic.main.video_playback_control.*
import kotlinx.android.synthetic.main.watch_video_activity.*

class WatchVideoActivity : AppCompatActivity() {

    private var playWhenReady: Boolean = true
    private var playerView: PlayerView? = null
    private var player: SimpleExoPlayer? = null
    private var videoTitle: String? = null
    private var playbackUrl: String? = null
    private var creatorName: String? = null
    private var creatorImgUrl: String? = null
    private var currentWindow: Int = 0
    private var playbackPosition: Long = 0
    private val PLAYER_USER_AGENT = "videocoin-orbital-android-demo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.watch_video_activity)
        var b: Bundle? = intent.extras
        playbackUrl = b?.getString(VIDEO_URL_KEY) //TODO: handle null url
        videoTitle = b?.getString(VIDEO_TITLE)
        creatorName = b?.getString(CREATOR_NAME)
        creatorImgUrl = b?.getString(CREATOR_IMG_URL)
        playerView = video_view
        back_button.setOnClickListener {
            releasePlayer()
            finish()
        }
        video_title.text = videoTitle
        creator_name.text = creatorName
        Glide.with(this).load(creatorImgUrl).placeholder(android.R.drawable.ic_menu_help).into(creator_profile_img)
        creator_profile_img.clipToOutline = true
    }

    companion object {
        const val VIDEO_TITLE = "video_title"
        const val VIDEO_URL_KEY = "video_url"
        const val CREATOR_NAME = "creator_name"
        const val CREATOR_IMG_URL = "creator_img_url"
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onStop() {
        super.onStop()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        releasePlayer()
    }

    private fun initializePlayer() {
        var drmSessionManager: DefaultDrmSessionManager<FrameworkMediaCrypto>? = null
        var renderersFactory = DefaultRenderersFactory(this)
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
        var trackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory())

        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(
                    this, renderersFactory, trackSelector, drmSessionManager)
            playerView?.player = player

            player?.playWhenReady = playWhenReady

            player?.seekTo(currentWindow, playbackPosition)

            val uri = Uri.parse(playbackUrl)
            var dataSourceFactory: DataSource.Factory = DefaultHttpDataSourceFactory(PLAYER_USER_AGENT)
            var hlsMediaSource = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            player?.prepare(hlsMediaSource, true, false)
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            playbackPosition = player?.currentPosition!!
            currentWindow = player?.currentWindowIndex!!
            player?.release()
            player = null
        }
    }
}