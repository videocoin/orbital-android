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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import io.videocoin.network.orbital.demo.R
import io.videocoin.network.orbital.demo.playback.WatchVideoActivity.Companion.VIDEO_URL_KEY
import kotlinx.android.synthetic.main.live_cast_ended_fragment.*

class LiveCastEndedFragment : Fragment() {

    private var playbackUrl: String? = null
    private var navController: NavController ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.live_cast_ended_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        playbackUrl = arguments?.getString(VIDEO_URL_KEY) //check if the playback url exists in the bundle

        if (playbackUrl == null) {
            view_ended_cast_button.visibility = View.GONE
        } else {
            view_ended_cast_button.visibility = View.VISIBLE
            view_ended_cast_button.setOnClickListener {
                navController?.navigate(R.id.watchVideoActivity2, arguments)
                activity?.finish()
            }
        }

        close_text.setOnClickListener {
            activity?.finish()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })
    }
}
