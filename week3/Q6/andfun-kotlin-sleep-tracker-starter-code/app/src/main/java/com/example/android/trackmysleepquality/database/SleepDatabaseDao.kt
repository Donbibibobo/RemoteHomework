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
 */

package com.example.android.trackmysleepquality.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/*
<一>
所有 Dao 都需要用關鍵字註釋，首先先將一些數據存入數據庫，我們將
添加一個帶註釋的插入法來插入單個 SleepNight，需要先使用 @Insert
註釋，還有一個「插入函數()」，並將要插入的內容放在參數位置傳入，在本
次程式要插入的是 data class SleepNight 之建構式，在編譯期間，
Room 會產生程式碼，將這個傳進來的 Kotlin object 變成 dataBase
中的一 row，當插入一個代表 SleepNight 的 row 之後，我們希望能夠更新
他，例如「新的結束時間」或是「睡眠品質」與插入幾乎時進行，所以接著使用
「@Update」註釋並添加「update()」方法並將想更新的資料傳入 entity 中
，在更新的時候，新物件可以除了 key 以外只有一個值不同或全都不同，其餘的操作
就必須使用 SQL quires 語法因為沒有預設「快捷」的方式，舉例來說「讀取數據庫」
就要用「@Query」來註解和實作。

<二>
寫一個「@Query」來用 key 取得「特定某一晚的資料」，然後加上「get(key)」
方法，並設定回傳一個 table row 作為 SleepNight 建構式的語法，然後加入
完整 SQL query 的 String 當做參數:
"SELECT * from daily_sleep_quality_table WHERE nightId = :key"
此語意為「從指定 table 選擇全部 column 並回傳指定 Id 的 row」，因為 key
是獨一無二，所以回傳只會有一個會是 null

<三>
接著我們希望能夠通過「刪除所有 row」而「不刪除 表」來清理 dataBase，我們
可以使用「@Delete」註釋，加上「delete(night: SleepNight)」讓我們刪除
特定 entity，但這個方法不太有效率，或是可以使用
「deleteAllNights(night: List<SleepNight>): Int」
來提供一個要刪除的 Night list，然後得到回傳刪了多少 row，這個做法的缺點是
我們必須獲取或知道 table 中的內容，這對刪除特定條目很有效，但對於「清空」來說
效率不好，既然我們不在乎表內的內容，我們只要使用「@Query」把全部刪除就好

<四>
使用 @Query 來刪除 table 中全部的內容，並連結 clear() 方法，用 SQL query 語法
「DELETE FROM daily_sleep_quality_table」因為沒有 WHERE 所以刪除 table 中
每一 row

<五>
還要有一個可以返回表中「所有 row」的 query，讓我們呈現所有紀錄過的晚上的資料，
我們想要回傳一個「排序過的(下行) entities」list，一樣用 SQL query 語法
「SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC」
這邊的回傳值，是 Room 一個很好的地方，就是可以回傳 LiveData，Room 會確保
dataBase 一更新，這些 LiveData 也會跟著更新，這表示我們只需要獲取這個
AllNight list 一次並替她加上一個「觀察者」，然後如果 Data 改變，UI 就會
自動更新以顯示

<六>
最後我們要通過查看 AllNights 來返回最近的一晚，SQL query 跟 <五> 相同但是
限制只要一筆資料，因為最近的一晚 Id 最大，所以可以透過「下行」來排序，回傳的
內容可以是 null，因為如果 <四> 清除了所有夜晚，就不會有內容
 */

@Dao
interface SleepDatabaseDao {
// 段落一
    @Insert
    fun insert(night: SleepNight)

    @Update
    fun update(night: SleepNight)

// 段落二
    @Query("SELECT * from daily_sleep_quality_table WHERE nightId = :key")
    fun get(key: Long): SleepNight

// 段落三
    //@Delete
    //fun deleteAllNights(night: List<SleepNight>): Int
    //fun delete(night: SleepNight)

// 段落四
    @Query("DELETE FROM daily_sleep_quality_table")
    fun clear()

// 段落五
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC")
    fun getAllNights(): LiveData<List<SleepNight>>

// 段落六
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1")
    fun getTonight(): SleepNight?
}
