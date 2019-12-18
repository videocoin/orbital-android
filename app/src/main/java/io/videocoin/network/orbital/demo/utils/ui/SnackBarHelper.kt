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

package io.videocoin.network.orbital.demo.utils.ui

import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import io.videocoin.network.orbital.demo.R
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity


class SnackBarHelper {

    companion object {
        fun createSnackBar(activity: FragmentActivity, view: View, message: String, actionText: String?,
                           actionListener: View.OnClickListener?): Snackbar {
            val snackbar = Snackbar.make(view, message, LENGTH_LONG)
            val snackView = snackbar.view
            snackView.setBackgroundColor(ContextCompat.getColor(activity, R.color.very_light_purple))
            val snackTextView: TextView = snackView.findViewById(R.id.snackbar_text)
            snackTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    activity.resources.getDimension(R.dimen.snackbar_text_size))
            snackTextView.setTextColor(ContextCompat.getColor(activity, R.color.dark_purple))
            val typefaceHindBold = ResourcesCompat.getFont(activity, R.font.hind_bold)
            snackTextView.setTypeface(typefaceHindBold)
            if (!TextUtils.isEmpty(actionText) && actionListener != null) {
                snackbar.setAction(actionText, actionListener)
                val snackActionView: TextView = snackView.findViewById(R.id.snackbar_action)
                snackActionView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        activity.resources.getDimension(R.dimen.snackbar_text_size))
            }
            return snackbar
        }
    }
}