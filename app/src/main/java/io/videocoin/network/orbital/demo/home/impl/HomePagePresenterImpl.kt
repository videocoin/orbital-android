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

import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.videocoin.network.orbital.demo.data.provider.DataAccessorProvider
import io.videocoin.network.orbital.demo.data.model.VideoData
import io.videocoin.network.orbital.demo.data.provider.DataAccessor
import io.videocoin.network.orbital.demo.home.HomePagePresenter
import io.videocoin.network.orbital.demo.home.HomePageView


class HomePagePresenterImpl(val homepageView: HomePageView, accessible: DataAccessorProvider) : HomePagePresenter {

    private var dataAccessor: DataAccessor = accessible.getDataAccessor()
    private var disposables: CompositeDisposable = CompositeDisposable()
    private var resultList : ArrayList<HomePageListItem> = ArrayList()

    override fun fetchItems() {
        resultList.clear()
        homepageView.showProgress()
        resultList.add(HomePageListItem(HomePageListItem.ListItemViewType.PAGE_TITLE_SECTION))
        dataAccessor.getLiveCasts(LiveCastsSubscriber())
    }

    override fun getUserImgUrl(): String? {
        return dataAccessor.getCurrentUser()?.profileImgUrl
    }

    override fun getUserDisplayName() : String? {
        return dataAccessor.getCurrentUser()?.name
    }

    private inner class LiveCastsSubscriber : Observer<ArrayList<VideoData>> {

        override fun onComplete() {

        }

        override fun onSubscribe(d: Disposable) {
            disposables.add(d)
        }

        override fun onNext(list: ArrayList<VideoData>) {
            resultList.add(LiveCastsListSection(HomePageListItem.ListItemViewType.LIVE_CASTS, list))
            dataAccessor.getRecentlyEnded(RecentlyEndedItemsSubscriber())
        }

        override fun onError(e: Throwable) {
            homepageView.dataRefreshComplete()
            homepageView.hideProgress()
            homepageView.showError()
        }
        
    }

    private inner class RecentlyEndedItemsSubscriber : Observer<ArrayList<VideoData>> {

        override fun onComplete() {

        }

        override fun onSubscribe(d: Disposable) {
            disposables.add(d)
        }

        override fun onNext(list: ArrayList<VideoData>) {
            if (list != null && list.size > 0) {
                resultList.add(HomePageListItem(HomePageListItem.ListItemViewType.RECENTLY_ENDED_TITLE))
            }
            for (item in list) {
                resultList.add(RecentlyEndedListItem(HomePageListItem.ListItemViewType.RECENTLY_ENDED_ITEM, item))
            }
            homepageView.dataRefreshComplete()
            homepageView.hideProgress()
            homepageView.onFetchedAllItems(resultList)
        }

        override fun onError(e: Throwable) {
            homepageView.dataRefreshComplete()
            homepageView.hideProgress()
            homepageView.showError()
        }

    }

    override fun destroy() {
       disposables.clear()
    }
}