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

package com.example.android.trackmysleepquality.sleepquality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/* 就像我們的 SleepTrackerViewModel 一樣，我們要從 Factory 傳入數據庫來達到分離，
   此外我們還需要傳入從導航中獲得的「sleepNightKey」，新增 Job、scope、結束 Job，
   現在我們想要紀錄「睡眠品質」後導航回 SleepTrackerFragment，想之前一樣，要創建一個
   navigateToSleepTracker 和 完成導航的方法 */
class SleepQualityViewModel(
    private val sleepNightKey: Long = 0L,
    val database: SleepDatabaseDao): ViewModel() {
        // Job for coroutines
        private  val viewModelJob = Job()
        // coroutines UI scope
        private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    // ViewModel 結束 coroutines 也結束
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    //sleepInfo
    val sleepInfo = MutableLiveData<String>()



    private val _navigateToSleepTracker = MutableLiveData<Boolean?>()
    val navigateToSleepTracker: LiveData<Boolean?>
        get() = _navigateToSleepTracker

    fun doneNavigation() {
        _navigateToSleepTracker.value = null
    }

    /* 最後使用相同的協程模式來創建「點擊」，我們在 UI scope 內啟動協程，切換到 IO，
    使用 get(sleepNightKey) 得到「今晚」，把「今晚」的「sleepQuality」設定為使用者按的
    結果，最後更新數據庫，並觸發導航為 true
     */
    fun onSetSleepQuality(quality: Int) {
        uiScope.launch {
            withContext(Dispatchers.IO){
                val tonight = database.get(sleepNightKey) ?: return@withContext
                tonight.sleepQuality = quality
                if (sleepInfo.value != null) {
                    tonight.sleepInformation = sleepInfo.value.toString()
                }
                database.update(tonight)
            }
            _navigateToSleepTracker.value = true
        }
    }

}


