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

package io.videocoin.network.orbital.demo.home.impl.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.videocoin.network.orbital.demo.R
import io.videocoin.network.orbital.demo.data.model.VideoData
import io.videocoin.network.orbital.demo.home.impl.HomePageAdapter
import kotlinx.android.synthetic.main.view_holder_live_cast_item.view.*

class LiveCastItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindDataToView(videoObj: VideoData, clickCallback: HomePageAdapter.ClickCallback) {
        itemView.video_title.text = videoObj.title
        Glide.with(itemView.context).load(videoObj.imageUrl).placeholder(R.drawable.video_thumbnail_placeholder).into(itemView.thumbnail)
        itemView.thumbnail.clipToOutline = true
        itemView.creator_name.text = videoObj.creatorName
        Glide.with(itemView.context).load(videoObj.creatorPhotoUrl).placeholder(android.R.drawable.ic_menu_help).into(itemView.creator_profile_img)
        itemView.creator_profile_img.clipToOutline = true
        itemView.setOnClickListener{clickCallback.videoItemClicked(videoObj)}
    }
}