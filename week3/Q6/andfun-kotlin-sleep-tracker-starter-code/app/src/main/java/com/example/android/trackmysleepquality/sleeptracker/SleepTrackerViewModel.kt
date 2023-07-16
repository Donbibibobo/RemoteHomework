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

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.provider.SyncStateContract.Helpers.insert
import android.provider.SyncStateContract.Helpers.update
import android.text.method.TextKeyListener.clear
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for SleepTrackerFragment.
 */
/*
<一>
當「START」按鈕被按下時，我們希望調用 SleepTrackerViewModel 中的一個函式，該函式
會創建一個「新的一晚」instance 並將它儲存在數據庫中，我們將使用 coroutines，因為不
希望按下按鈕後會影響用戶介面。為了管理所有的 coroutines，我們需要一個 Job，這個
ViewModel Job() 允許我們在 ViewModel 不再使用和銷毀時取消由此 ViewModel 啟動的
所有 coroutines，這樣就不會得到「無處可返回」的 coroutines

<二>
當 ViewModel 銷毀時，onCleared 被調用，我們可以覆寫此方法來取消從 ViewModel 啟動
的所有 coroutines，接下來我們需要一個 scope 給 coroutines run，scope 決定
coroutines 會在哪個執行緒上運行並且還要了解 Job，為了得到這個 scope，我們要求一個
CoroutineScope 的 instance，然後傳入 Dispatcher 和 Job，Dispatchers.Main
代表 coroutines 發送到 UI scope 並在主執行緒中執行，這對從 ViewModel 啟動的
許多 coroutines 來說是明智的，因為他最終會在執行一些處理導致 UI 更新

<三>
還需要一個變數來保存當前的夜晚，我們創建為 LiveData 因為我們希望可以觀察他或更改他，
我們同時也想在創建 ViewModel 時獲得數據庫中的「所有夜晚」，我們定義過 getAllNights()
將返回 LiveData，如果有任何變化，Room 將會更新此 LiveData

<十>
nights 變數是 LiveData，Room 會負責更新數據，既然 nights 始終擁有最新數據，我們可以
將它顯示在 TextView 中，但那只會顯示「引用的物件」，所以為了看到物件內容，我們需要轉換
這些數據，這邊將轉換成 Strings，我們在下一行新增一個 nightsString 代表物件的文字內容
，然後使用 Transformations.map，每次 nights 從數據庫接收新數據時執行的「map 映射」
，在 Transformations Class 中我們將 nights 傳入 map() 方法中，並定義 mapping()
函式調用 formatNights，我們提供參數 nights 和 resources object，因為這樣才能訪問
到「String」資源


<四>
我們需要盡快設置 tonight 以便我們可以使用它，所以在 init 中執行，initializeTonight()
內部我們使用設計好的 coroutines uiScope 從數據庫獲取今晚的數據，所以當我們等待時就不會
阻塞 UI。用 .launch 在指定的 scope 中啟動 coroutines，他會馬上執行且不會阻塞現有執行緒
，所以我們在裡面取得當晚的值

<五>
我們希望 getTonightFromDatabase() 不會被阻塞，且回傳 sleepNight 或 Null，我們將此函數
標記為 suspend，因為我們希望從 coroutines 中調用而不是 Block，怎麼設定讓「那晚」回傳呢？
我們會使用 dispatcher.IO 在 IO context 中創建另一個 coroutines，並從 Dao 中調用
database.getTonight()，把數據庫中最新的夜晚放入變數 night 中。條件式表示如果開始和結束時間
相同，我們知道正在執行現有的夜晚，所以回傳 night 資料，如果開始和結束時間不同，表示沒有開始的
夜晚，所以回傳 null。最後回傳 night

<六>
現在可以處理「START」按鈕的程式了，我們希望按了按鈕可以創建一個新的「SleepNight」，我們想將它
插入 Database，並將他指派給「今晚」，我們調用函數 onStartTracking()，它的結構看起來會很像
initializeTonight，在這個函式中我們 .launch 一個 coroutine，因為接下來的程式都會很耗時，
特別是數據庫的操作，同樣的我們在 UI scope 中執行因為我們需要此結果來繼續更新 UI，我們建立一個
新的 SleepNight，他將獲得當前時間為開始時間，然後用 insert 來將他插入 database 中，這個
insert 也會是標記為 suspend 函式，最後我們通過調用 getTonightFromDatabase() 將今晚設置
為新的夜晚

<七>
現在來定義剛剛的 insert 方法，會看起來很像 getTonightFromDatabase()，他是 suspend 方法
，我們使用 dispatcher.IO，然後調用 Dao 方法的 insert。
到這邊可以看到一個模式，我們啟動一個在 UI 執行緒上運行的 coroutines，因為結果會影響 UI，在內部
我們調用 suspend 函式來執行長時間的工作，以便在等待期間不回阻塞 UI 執行緒，然後去定義 suspend
函式，因為長時間任務與 UI 無關，所以我們用「withContext」換到 IO context，然後我們可以在較優化
且特別為這種情況設置的「thread pool 共享的執行緒」中運行，在裡面就可以運行 database.insert(night)
這種數據庫的操作了

<八>
讓我們來創建另外兩個按鈕「STOP」和「CLEAR」，onStopTracking() 也是同一套協程模式，注意返回
的註釋，在 kotlin 中，return@lable 語法用於指定該語句從多個嵌套函數中返回哪個函數，這邊我們特別
指定從 launch 來回傳，而不是 Lambda（默認回傳最後一行），我們更新當前時間為結束時間，並用
update(oldNight) 來更新數據庫，最後再寫一個 update()，寫法跟 insert() 一樣

<九>
一樣的方式去做 onClear() 和 clear。現在，在 xml 佈局中要怎麼把 START 按鈕跟 onStartTracking()
連再一起呢？

 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {
// 一
        private var viewModelJob = Job()
// 二
        override fun onCleared() {
                super.onCleared()
                viewModelJob.cancel()
        }
        private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
// 三
        private var tonight = MutableLiveData<SleepNight?>()
        private val nights = database.getAllNights()
// 十
        val nightsString = Transformations.map(nights) { nights ->
                formatNights(nights, application.resources)
        }
// 四
        init {
            initializeTonight()
        }
        private fun initializeTonight() {
                uiScope.launch {
                        tonight.value = getTonightFromDatabase()
                }
        }
// 五
        private suspend fun getTonightFromDatabase(): SleepNight? {
                return withContext(Dispatchers.IO) {
                        var night = database.getTonight()
                        if (night?.endTimeMilli != night?.startTimeMilli) {
                                night = null
                        }
                        night
                }
        }
// 六
        fun onStartTracking() {
                uiScope.launch {
                        val newNight = SleepNight()

                        insert(newNight)

                        tonight.value = getTonightFromDatabase()
                }
        }
// 七
        private suspend fun insert(night: SleepNight) {
                withContext(Dispatchers.IO) {
                        database.insert(night)
                }
        }
// 八
        fun onStopTracking() {
                uiScope.launch {
                        val oldNight = tonight.value?: return@launch

                        oldNight.endTimeMilli = System.currentTimeMillis()

                        update(oldNight)

                        //
                        _navigateToSleepQuality.value = oldNight
                }
        }
        private suspend fun update(night: SleepNight) {
                withContext(Dispatchers.IO) {
                        database.update(night)
                }
        }
// 九
        fun onClear() {
                uiScope.launch {
                        clear()
                        tonight.value = null

                        _showSnackbarEvent.value = true
                }
        }
        suspend fun clear() {
                withContext(Dispatchers.IO) {
                        database.clear()
                }
        }
/* 添加「導航 Event」的 LiveData。將其設置為 private，因為不想將「設置」功能給 Fragment，
  再創建一個只有 getter() 的公有變數，導航後，我們希望立即重置導航變數，所以使用
  doneNavigating()，至於停止按鈕 onStopTracking()，我們需要觸發此導航，我們在該方法中
  的最後一行新增「_navigateToSleepQuality.value = oldNight」，

 */
        private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
        val navigateToSleepQuality: LiveData<SleepNight>
                get() = _navigateToSleepQuality
        fun doneNavigating() {
                _navigateToSleepQuality.value = null
        }



        /* 添加「按鈕隱藏」、「有資料才有清除的按鈕」方法，並為他們加上 Transformations.map
           ，變數的狀態會隨著 tonight 的狀態改變，tonight 一開始為 null，這樣的情況下我們希望
           「START」是可見的，如果 tonight 有值，「STOP」應為可見 ，記住紀錄 quality 之後，我們
           會重置 tonight，「CLEAR」按鈕只有有 nights 時才看得見*/
        val startButtonVisible = Transformations.map(tonight) {
                null == it
        }

        val stopButtonVisible = Transformations.map(tonight) {
                null != it
        }

        val clearButtonVisible = Transformations.map(nights) {
                it?.isNotEmpty()
        }


        /* 顯示 snack bar */
        private var _showSnackbarEvent = MutableLiveData<Boolean>()
        val showSnackbarEvent: LiveData<Boolean>
                get() = _showSnackbarEvent

        fun doneShowingSnackbar() {
                _showSnackbarEvent.value = false
        }
}


