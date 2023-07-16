///*
// * Copyright 2018, The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.example.android.trackmysleepquality
//
//import androidx.room.Room
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.platform.app.InstrumentationRegistry
//import com.example.android.trackmysleepquality.database.SleepDatabase
//import com.example.android.trackmysleepquality.database.SleepDatabaseDao
//import com.example.android.trackmysleepquality.database.SleepNight
//import org.junit.Assert.assertEquals
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import java.io.IOException
//
///**
// * This is not meant to be a full set of tests. For simplicity, most of your samples do not
// * include tests. However, when building the Room, it is helpful to make sure it works before
// * adding the UI.
// */
///*
//我們有一個測試類別，SleepDatabaseTest，「@RunWith」代表要我們使用的測試工具 JUnit4，
//在設置上用了「@Before」並使用 SleepDatabaseDao 創建 SleepDatabase 在記憶體中（意思
//是不會真的保存到文件系統上而是在記憶體中，並在測試結束後刪除），此外，在建構記憶體數據庫時，
//調用另一個方法「.allowMainThreadQueries()」，預設情況下如果嘗試在主執行緒中使用 queries
//會收到錯誤，調用後就不會有錯誤（此做法最好是只在測試中使用），
//
//「@Test」中我們「創建」、「插入」和「檢索」SleepNight，並「assert 斷言」他們是相同的，
//如果有異常就「@Throws」出 Exception，最後當測試完成時，執行「@After」來關閉數據庫
// */
//
//@RunWith(AndroidJUnit4::class)
//class SleepDatabaseTest {
//
//    private lateinit var sleepDao: SleepDatabaseDao
//    private lateinit var db: SleepDatabase
//
//    @Before
//    fun createDb() {
//        val context = InstrumentationRegistry.getInstrumentation().targetContext
//        // Using an in-memory database because the information stored here disappears when the
//        // process is killed.
//        db = Room.inMemoryDatabaseBuilder(context, SleepDatabase::class.java)
//                // Allowing main thread queries, just for testing.
//                .allowMainThreadQueries()
//                .build()
//        sleepDao = db.sleepDatabaseDao
//    }
//
//    @After
//    @Throws(IOException::class)
//    fun closeDb() {
//        db.close()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun insertAndGetNight() {
//        val night = SleepNight()
//        sleepDao.insert(night)
//        val tonight = sleepDao.getTonight()
//        assertEquals(tonight?.sleepQuality, -1)
//    }
//}
//
