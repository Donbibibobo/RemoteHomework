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

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://mars.udacity.com/"


enum class MarsApiFilter(val value: String) { SHOW_RENT("rent"), SHOW_BUY("buy"), SHOW_ALL("all") }


/* 建構一個 Moshi builder，為了使 Moshi 的註釋在 Kotlin 中正常運作，
   所以加入 KotlinJsonAdapterFactory()，然後 .build */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


/* 在我們的例子中，我們希望 retrofit 從伺服器獲取 JSON 並將其作為 String 反回。
    retrofit 有一個 ScalarsConverter 支持返回字串和其他原始類型，所以我們在
    buidler 上呼叫 .addConverterFactory() 並傳一個 instance
    「ScalarsConverterFactory.create()」當參數參數，然後呼叫 BASE_URL 來
    指定伺服器端的地址，最後用 .build() 來建立 retrofit 物件 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

/* 定義一個介面來解釋 retrofit 如何使用 HTTP 請求來與伺服器溝通，retrofit 會創建
    一個實作了我們的介面的物件，概念上類似於 Room 實現了我們的 dao，我們稱這個介面為
    MarsAPIService，因為他定義了 retrofit 伺服器要創建的 API。因為我們現在的目標
    是得到 JSON 轉成的 String，我們只要定義一個方法去完成就好，名稱定為
    getProperties()，為了告訴 retrofit 這個方法要怎麼實作，我們用 @GET 來註記，
    並指定我們希望此方法使用的「path」或「endpoint」，對於 JSON 的 response 是
    「realestate 房地產」，每當我們調用 getProperties() 方法，retrofit 會將這個
    「realestate」追加到 BASE_URL 中，建立一個 call 後面放告訴他未來我基奧這個方法
    要回傳 String*/
/* 定義一個 MarsApi 服務(未來的存取方法)，服務內容是得到 Properties 並回傳 String*/
interface MarsApiService {
    @GET("realestate")
    fun getProperties(@Query("filter") type: String):
            Deferred<List<MarsProperty>>
}
/* 寫好上述的 API 之後，要使用他們要先創建一個 retrofit 服務，我們調用
   retrofit.create，把剛剛定義的「伺服器介面 API」傳進去，原本的產生順序如下：
    // 產生一個 剛剛 API 的 retrofit 服務（這是實作好的介面）
    val marsApiService = retrofit.create(MarsApiService::class.java)
    // 當我需要他的時候，這樣呼叫服務來得到資料
    val marsData = marsApiService.getProperties().execute().body()
*/

/* 因為 retrofit.create 非常耗資源，而且我們的 App 只需要一個
   retrofit 服務的 instance，我們使用名為 MarsApi 的
   公開物件，新增一個 lazy initialized (要被使用時才會被初始化)，
   目的是讓這個 retrofit.create 被用到時才產生，呼叫
   MarsApi.retrofitService 將返回一個 MarsApiService，
   也就是被實作好的 MarsApiService*/
object MarsApi {
    val retrofitService: MarsApiService by lazy {
        retrofit.create(MarsApiService::class.java)
    }
}

