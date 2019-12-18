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

package io.videocoin.network.orbital.demo.home.impl

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import io.videocoin.network.orbital.demo.data.provider.DataAccessorProvider
import io.videocoin.network.orbital.demo.R
import io.videocoin.network.orbital.demo.data.model.VideoData
import io.videocoin.network.orbital.demo.home.HomePagePresenter
import io.videocoin.network.orbital.demo.home.HomePageView
import io.videocoin.network.orbital.demo.livecast.LiveCastActivity
import io.videocoin.network.orbital.demo.login.impl.LoginActivity
import io.videocoin.network.orbital.demo.playback.WatchVideoActivity.Companion.CREATOR_IMG_URL
import io.videocoin.network.orbital.demo.playback.WatchVideoActivity.Companion.CREATOR_NAME
import io.videocoin.network.orbital.demo.playback.WatchVideoActivity.Companion.VIDEO_TITLE
import io.videocoin.network.orbital.demo.playback.WatchVideoActivity.Companion.VIDEO_URL_KEY
import io.videocoin.network.orbital.demo.utils.ui.SnackBarHelper
import kotlinx.android.synthetic.main.homepage_fragment.*
import kotlinx.android.synthetic.main.view_confirmation_dialog.view.*

class HomePageFragment : Fragment(), HomePageView, SwipeRefreshLayout.OnRefreshListener, HomePageAdapter.ClickCallback {

    private var presenter: HomePagePresenter?= null
    private var navController : NavController?= null
    private var snackBar: Snackbar?= null
    private var recyclerView: RecyclerView?= null
    private var start_livecast_button : TextView?= null
    private var logoutDialog: AlertDialog ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.homepage_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        presenter = HomePagePresenterImpl(this, activity?.application as DataAccessorProvider)
        start_livecast_button = view.findViewById(R.id.start_live_cast_button)
        start_livecast_button?.setOnClickListener {
           openStartLiveCastActivity()
        }
        recyclerView = view.findViewById(R.id.recycler_list)
        recyclerView?.layoutManager = LinearLayoutManager(view.context)
        val spacing = getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing)
        val spaceDecoration = SpacesItemDecoration(spacing)
        recyclerView?.addItemDecoration(spaceDecoration)
        recycler_refresh?.setOnRefreshListener(this)

        presenter?.fetchItems()
    }

    override fun onDestroy() {
        presenter?.destroy()
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        if (logoutDialog != null) {
            logoutDialog!!.dismiss()
        }
        if (snackBar != null) {
            snackBar!!.dismiss()
        }
    }

    override fun onFetchedAllItems(resultList: List<HomePageListItem>) {
        if (activity != null) {
            recyclerView?.visibility = View.VISIBLE
            recyclerView?.adapter = HomePageAdapter(resultList, presenter?.getUserImgUrl(), this, activity as Context)
        }
    }

    override fun showProgress() {
        progress_circular?.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress_circular?.visibility = View.GONE
    }

    override fun onRefresh() {
        recyclerView?.visibility = View.GONE
        presenter?.fetchItems()
        recycler_refresh?.isRefreshing = true
        progress_circular?.visibility = View.GONE
    }

    override fun dataRefreshComplete() {
        recycler_refresh?.isRefreshing = false
    }

    //HomepageAdapter ClickCallback impl
    override fun videoItemClicked(videoObj: VideoData) {
        var watchVideoPageBundle = Bundle()
        watchVideoPageBundle.putString(VIDEO_URL_KEY, videoObj.playbackUrl)
        watchVideoPageBundle.putString(VIDEO_TITLE, videoObj.title)
        watchVideoPageBundle.putString(CREATOR_NAME, videoObj.creatorName)
        watchVideoPageBundle.putString(CREATOR_IMG_URL, videoObj.creatorPhotoUrl)
        navController?.navigate(R.id.watchVideoActivity, watchVideoPageBundle)
    }

    override fun profileButtonClicked() {
        showLogoutConfirmationDialog()
    }

    override fun videocoinMktgIconClicked() {
        navController?.navigate(R.id.mktgPageFragment)
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(context!!)
        val customLayout = layoutInflater.inflate(R.layout.view_confirmation_dialog, null)
        builder.setView(customLayout)
        logoutDialog = builder.create()
        customLayout.conf_button.setOnClickListener {
            logoutDialog?.dismiss()
            doLogout()
        }
        customLayout.dialog_msg.text = presenter?.getUserDisplayName() ?: ""
        logoutDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        logoutDialog?.show()
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.8f).toInt()
        logoutDialog?.getWindow()?.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
    }

    private fun doLogout() {
        var auth: FirebaseAuth = FirebaseAuth.getInstance()
        auth.signOut()

        var googleSignInClient: GoogleSignInClient
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(activity!!, googleSignInOptions);
        googleSignInClient.signOut().addOnCompleteListener {
            activity!!.finish()
            showLogin()
        }
    }

    private fun showLogin() {
        val loginIntent = Intent(activity, LoginActivity::class.java)
        startActivity(loginIntent)
    }
    //end ClickCallback impl

    private fun openStartLiveCastActivity() {
        val startLiveCastIntent = Intent(activity, LiveCastActivity::class.java)
        startActivity(startLiveCastIntent)
    }

    override fun showError() {
        showSnackbarMessage(getString(R.string.error_fetching_data))
    }

    private fun showSnackbarMessage(msg: String) {
        if (snackBar == null) {
            snackBar = SnackBarHelper.createSnackBar(activity!!, snackbar_action!!, msg, null, null)
        }
        snackBar?.setText(msg)
        snackBar?.show()
    }
}

class SpacesItemDecoration(private val mSpace: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.bottom = mSpace;
    }
}