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

package io.videocoin.android.app.demo.data.model

import com.google.gson.annotations.SerializedName

class StreamData {

    @SerializedName("id")
    var id: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("input_url")
    var inputUrl: String? = null
    @SerializedName("output_url")
    var outputUrl: String? = null
    @SerializedName("stream_contract_id")
    var streamContractId: String? = null
    @SerializedName("stream_contract_address")
    var streamContractAddress: String? = null
    @SerializedName("status")
    var status: String? = null
    @SerializedName("input_status")
    var inputStatus: String? = null
    @SerializedName("created_at")
    var createdAt: String? = null
    @SerializedName("updated_at")
    var updatedAt: String? = null
    @SerializedName("ready_at")
    var readyAt: Any? = null
    @SerializedName("completed_at")
    var completedAt: Any? = null
    @SerializedName("rtmp_url")
    var rtmpUrl: String? = null

    companion object {
        const val STREAM_STATUS_NEW = "STREAM_STATUS_NEW"
        const val STREAM_STATUS_PREPARING = "STREAM_STATUS_PREPARING"
        const val STREAM_STATUS_PREPARED = "STREAM_STATUS_PREPARED"
        const val STREAM_STATUS_FAILED = "STREAM_STATUS_FAILED"
        const val STREAM_STATUS_CANCELLED = "STREAM_STATUS_CANCELLED"
        const val STREAM_STATUS_READY = "STREAM_STATUS_READY"
        const val STREAM_STATUS_COMPLETED = "STREAM_STATUS_COMPLETED"
    }

}
