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

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import io.videocoin.network.orbital.demo.data.provider.DataAccessorProvider
import io.videocoin.network.orbital.demo.R
import io.videocoin.network.orbital.demo.data.model.VideoData
import io.videocoin.network.orbital.demo.livecast.CreateLivecastFragment.Companion.TITLE_KEY
import io.videocoin.network.orbital.demo.playback.WatchVideoActivity
import io.videocoin.network.orbital.demo.utils.ui.SnackBarHelper
import kotlinx.android.synthetic.main.go_live_fragment.*

class GoLiveFragment : Fragment(), GoLiveView {

    private lateinit var navController : NavController
    private lateinit var liveCastTitle : String
    private var presenter: GoLivePresenter? = null
    private var snackbar: Snackbar?= null
    private var MULTIPLE_PERMISSIONS_REQUEST_CODE = 5557

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.go_live_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        liveCastTitle = arguments?.getString(TITLE_KEY) ?: "My Live Cast"

        navController = Navigation.findNavController(view)

        presenter = GoLivePresenterImpl(this, activity?.application as DataAccessorProvider)

        back_button.setOnClickListener { navController.navigateUp() }
        end_button.setOnClickListener {
            time_indicator.stop()
            presenter?.endLiveCast()
        }

        go_live_button.setOnClickListener { presenter?.goLive(liveCastTitle) }

        go_live_group.visibility = View.VISIBLE
        going_live_group.visibility = View.GONE
        now_live_group.visibility = View.GONE

        page_title.setText(R.string.go_live)

        if (activity != null) {
            requestPermissionsToLiveCast(activity!!)
        }

    }

    private fun requestPermissionsToLiveCast(activity: FragmentActivity) {
        if (checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //all permissions granted, can start camera
            presenter?.setupCameraDataPublisher(camera_view)

        } else {
            requestPermissions(getSetOfUngrantedPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)), MULTIPLE_PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun getSetOfUngrantedPermissions(listOfAllPermissionsNeeded: Array<String>) : Array<String?> {
        var result = ArrayList<String>()
        for (perm in listOfAllPermissionsNeeded) {
            if (checkSelfPermission(activity!!, perm) != PackageManager.PERMISSION_GRANTED) {
                result.add(perm)
            }
        }
        val resArray = arrayOfNulls<String>(result.size)
        result.toArray(resArray) //convert arraylist to array
        return resArray
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MULTIPLE_PERMISSIONS_REQUEST_CODE) {
            if (!grantResults.isEmpty()) {
                for (grantResult in grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        showSnackbarMessage(getString(R.string.permissions_error))
                        return
                    }
                }
                presenter?.setupCameraDataPublisher(camera_view)
            } else {
                showSnackbarMessage(getString(R.string.permissions_error))
            }
        }
    }

    override fun showGoLiveView() {
        go_live_group.visibility = View.VISIBLE
        going_live_group.visibility = View.GONE
        now_live_group.visibility = View.GONE
        page_title.setText(R.string.go_live)
    }

    override fun showGoingLiveProgress() {
        go_live_group.visibility = View.GONE
        going_live_group.visibility = View.VISIBLE
        now_live_group.visibility = View.GONE
        page_title.setText(R.string.going_live)
    }

    override fun showLiveNowView() {
        go_live_group.visibility = View.GONE
        going_live_group.visibility = View.GONE
        now_live_group.visibility = View.VISIBLE
        page_title.setText(R.string.now_live)
        time_indicator.base = SystemClock.elapsedRealtime()
        time_indicator.start()
    }

    override fun showLiveCastEndedView(videoObj: VideoData?) {
        if (videoObj != null) {
            var watchVideoPageBundle = Bundle()
            watchVideoPageBundle.putString(WatchVideoActivity.VIDEO_URL_KEY, videoObj.playbackUrl)
            watchVideoPageBundle.putString(WatchVideoActivity.VIDEO_TITLE, videoObj.title)
            watchVideoPageBundle.putString(WatchVideoActivity.CREATOR_NAME, videoObj.creatorName)
            watchVideoPageBundle.putString(WatchVideoActivity.CREATOR_IMG_URL, videoObj.creatorPhotoUrl)
            navController?.navigate(R.id.liveCastEndedFragment, watchVideoPageBundle)
        }
        else {
            navController?.navigate(R.id.liveCastEndedFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter?.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter?.onPause()
    }

    override fun showErrorCreatingLiveCast() {
       showSnackbarMessage(getString(R.string.error_creating_livecast))
    }

    private fun showSnackbarMessage(msg: String) {
        if (snackbar == null) {
            snackbar = SnackBarHelper.createSnackBar(activity!!, snackbar_action!!, msg, null, null)
        }
        snackbar?.setText(msg)
        snackbar?.show()
    }

}