/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.marsrealestate

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.marsrealestate.network.MarsProperty
import com.example.android.marsrealestate.overview.MarsApiStatus
import com.example.android.marsrealestate.overview.PhotoGridAdapter

/*
<一>
首先新增 @BindingAdapter 註釋，並給定 imageURL 作為參數，這會告訴
 data binding 當 xml 檔案中有 imageUrl 屬性時執行此 BindingAdapter
 ，創建函式 bindImage，指定參數是 ImageView，所以只有 ImageView 或其
 子類別能使用這個 BindingAdapter，另一個參數是 URL 網址

 <二>
 寫 let{} 區塊來解決 Uri 可能為 null 的問題
 Glide 需要一個「Uri object」，因此在 Adapter 中我們先將 imaUrl 轉換
 成 Uri。toUri() 是來自 Android KTX Core Library 的 Kotlin 擴展函式
 ，且必須確保生成的 Uri 具有「https」「scheme 方案」，因為我們從中提取圖片
 的伺服器需要「https」，所以使用「.buildUpon().scheme("https")」加到
 toUri() 後面來做到這點

<三>
現在可以使用 Glide 了，Glide 具有流暢的介面，意思是它具有可讀語法的鏈式調用，
要使用 Glide 加載圖片，調用 Glide.with，load 讀取 Uri，into 送進 imgView

<四>
在 .load(imgUri) 後面新增「加載中」或「加載錯誤」的圖片，使用 .apply 然後
放進 RequestOptions() 再分別放入相應圖片

像這樣加載圖也是為什麼 Glide 需要 Android 的 context 的原因之一

 */

/* 此 Adapter 只需要將 RecyclerView adapter 「快取」到 PhotoGridAdapter，
    然後就可以使用更新的列表數據來調用 adapter.submitList*/
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<MarsProperty>?) {
    val adapter = recyclerView.adapter as PhotoGridAdapter
    adapter.submitList(data)
}


@BindingAdapter("imageUrl")      // 一
fun bindImage(imgView: ImageView, imaUrl: String?) {
    imaUrl?.let{         // 二
        val imgUri = it.toUri().buildUpon().scheme("https").build() //把 imaUrl 變成 Uri
        Glide.with(imgView.context)     // 三
            .load(imgUri)
            .apply(RequestOptions()    // 四
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image))
            .into(imgView)
    }
}

@BindingAdapter("marsApiStatus")
fun bindStatus(statusImageView: ImageView, status: MarsApiStatus?) {
    when (status) {
        MarsApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        MarsApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        MarsApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}

