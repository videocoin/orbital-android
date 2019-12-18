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
import com.bumptech.glide.Glide
import io.videocoin.network.orbital.demo.home.impl.HomePageAdapter
import kotlinx.android.synthetic.main.viewholder_page_title_section.view.*

class PageTitleSectionViewHolder(itemView: View, val title: String, private val photoUrl: String?, private val clickCallback: HomePageAdapter.ClickCallback) : HomepageItemBaseViewHolder(itemView) {

    override fun onBindViewHolder() {
        itemView.live_casts_title.text = title

        Glide.with(itemView.context)
                .load(photoUrl)
                .placeholder(android.R.drawable.ic_menu_help)
                .into(itemView.user_profile_img)
        itemView.user_profile_img.clipToOutline = true
        itemView.user_profile_img.setOnClickListener { clickCallback.profileButtonClicked() }

        itemView.about_videocoin.setOnClickListener {
            clickCallback.videocoinMktgIconClicked()
        }
    }
}