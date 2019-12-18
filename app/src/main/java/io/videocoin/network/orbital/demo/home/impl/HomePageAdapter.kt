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
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.videocoin.network.orbital.demo.R
import io.videocoin.network.orbital.demo.data.model.VideoData
import io.videocoin.network.orbital.demo.home.impl.viewholders.*

class HomePageAdapter(val items : List<HomePageListItem>, private val userImgUrl: String?, private val clickCallback: ClickCallback, private val context: Context) : RecyclerView.Adapter<HomepageItemBaseViewHolder>() {

    override fun onBindViewHolder(holder: HomepageItemBaseViewHolder, pos: Int) {
        if (holder is LiveCastsSectionViewHolder) {
            holder.liveCastItemsList = (items[pos] as LiveCastsListSection).liveCastItemsList
        }
        if (holder is RecentlyEndedItemViewHolder) {
            holder.videoObj = (items[pos] as RecentlyEndedListItem).videoObj
        }
        holder.onBindViewHolder()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomepageItemBaseViewHolder {
        val type = HomePageListItem.ListItemViewType.fromInt(viewType)

        val viewHolder: HomepageItemBaseViewHolder

        when (type) {
            HomePageListItem.ListItemViewType.PAGE_TITLE_SECTION -> {
                val view = LayoutInflater.from(context).inflate(R.layout.viewholder_page_title_section, parent, false)
                viewHolder = PageTitleSectionViewHolder(view, context.getString(R.string.live_casts), userImgUrl, clickCallback)
            }
            HomePageListItem.ListItemViewType.LIVE_CASTS -> {
                val view = LayoutInflater.from(context).inflate(R.layout.section_horizontal_list, parent, false)
                viewHolder = LiveCastsSectionViewHolder(view, clickCallback)
            }
            HomePageListItem.ListItemViewType.RECENTLY_ENDED_TITLE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.view_holder_section_title, parent, false)
                viewHolder = SectionTitleViewHolder(view, context.getString(R.string.recently_ended))
            }
            HomePageListItem.ListItemViewType.RECENTLY_ENDED_ITEM -> {
                val view = LayoutInflater.from(context).inflate(R.layout.view_holder_recently_ended_item, parent, false)
                viewHolder = RecentlyEndedItemViewHolder(view, clickCallback)
            }
        }

        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {
        return items.get(position).type.ordinal
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface ClickCallback {
        fun videoItemClicked(videoObj: VideoData)
        fun profileButtonClicked()
        fun videocoinMktgIconClicked()
    }

}
