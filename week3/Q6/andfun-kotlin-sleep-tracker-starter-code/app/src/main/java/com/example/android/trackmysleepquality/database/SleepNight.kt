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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/* 在 database package 中建立一個 SleepNight 的 data class，在裡面新增
一個 nightId 變數，且設定為 Long 才能自動生成密鑰，並且我們要初始化他，所以
設為 0，然後添加一個 startTimeMilli 變數表示開始時間（毫秒為單位），並用
System.currentTimeMillis() 來初始化給他「現在時間」，接下來新增 endTimeMilli
變數並初始化與 startTimeMilli 相同，來保證 System.currentTimeMillis() 的不同，
最後新增 sleepQuality 並初始化為 -1，來表示尚未紀錄睡眠品質

為了使 SleepNight 類別可以被 room 用來創建 table，加上 @Entity 註釋，並且不用
預設的名字而是自己為 table 命名(名稱最好要包含 "table")，再來是使用 @PrimaryKey
，這個是強制要設定的，我們使用在 nightId，並使它自動生成密鑰，room 會為這個 class
所有的「instance 建構」生成一個特殊的 Key，代表 table 每一不同的 row，剩下的變數
就都用 @ColumnInfo 來註釋，並為他們提供自訂的名字
 */
@Entity(tableName = "daily_sleep_quality_table")
data class SleepNight(
    @PrimaryKey(autoGenerate = true)
    var nightId: Long = 0L,

    @ColumnInfo(name = "start_time_milli")
    val startTimeMilli: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "end_time_milli")
    var endTimeMilli: Long = startTimeMilli,

    @ColumnInfo(name = "quality_rating")
    var sleepQuality: Int = -1,

    @ColumnInfo(name = "sleep_information")
    var sleepInformation: String = "default"
    )