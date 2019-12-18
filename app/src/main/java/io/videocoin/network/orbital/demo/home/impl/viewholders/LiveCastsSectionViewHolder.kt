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

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.videocoin.network.orbital.demo.R
import io.videocoin.network.orbital.demo.data.model.VideoData
import io.videocoin.network.orbital.demo.home.impl.HomePageAdapter

class LiveCastsSectionViewHolder(itemView: View, val clickCallback: HomePageAdapter.ClickCallback) : HomepageItemBaseViewHolder(itemView) {

    lateinit var liveCastItemsList: List<VideoData>

    override fun onBindViewHolder() {
        bindDataToView()
    }

    fun bindDataToView() {
        var recyclerView: RecyclerView = itemView.findViewById(R.id.horizontal_list)
        recyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        val spacing = itemView.context.getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing)
        val spaceDecoration = VerticalSpacesItemDecoration(spacing)
        if (recyclerView.itemDecorationCount == 0) {
            recyclerView.addItemDecoration(spaceDecoration)
        }
        recyclerView.adapter = LiveCastsAdapter(liveCastItemsList, clickCallback, itemView.context)
    }
}

class LiveCastsAdapter(val items : List<VideoData>, val clickCallback: HomePageAdapter.ClickCallback, val context: Context) : RecyclerView.Adapter<LiveCastItemViewHolder>() {

    override fun onCreateViewHolder(p: ViewGroup, p1: Int): LiveCastItemViewHolder {
        val view = LayoutInflater.from(p.context).inflate(R.layout.view_holder_live_cast_item, p, false)
        return LiveCastItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: LiveCastItemViewHolder, pos: Int) {
        var obj: VideoData = items.get(pos)
        holder.bindDataToView(obj, clickCallback)
    }

}

class VerticalSpacesItemDecoration(private val mSpace: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.right = mSpace;
    }
}