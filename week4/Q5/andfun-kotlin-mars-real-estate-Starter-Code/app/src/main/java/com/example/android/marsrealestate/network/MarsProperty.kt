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

package com.example.android.marsrealestate.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/* 在 Moshi 中按名稱匹配 JSON 的資料，並選定適當的類型，JSON 有些
    名稱可能會與我們的風格不太依樣，此時就可以用 @JSON 註釋「JSON
    實際上的名稱」，然後再放自己想要的名稱 */
@Parcelize
data class MarsProperty(
    val id: String,
    //val img_src: String,
    @Json(name="img_src") val imgSrcUrl: String,
    val type: String,
    val price: Double): Parcelable {
        val isRental
            get() = type == "rent"
    }

