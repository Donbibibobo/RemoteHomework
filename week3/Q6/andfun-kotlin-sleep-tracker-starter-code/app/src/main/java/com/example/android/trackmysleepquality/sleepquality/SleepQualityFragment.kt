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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepQualityBinding

/**
 * Fragment that displays a list of clickable icons,
 * each representing a sleep quality rating.
 * Once the user taps an icon, the quality is set in the current sleepNight
 * and the database is updated.
 */
class SleepQualityFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepQualityBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_quality, container, false)

        val application = requireNotNull(this.activity).application

        /* 我們需要獲取導航附帶的參數，他們在 SleepQualityFragment 中，從 Bundle 中取得 */
        val arguments = SleepQualityFragmentArgs.fromBundle(arguments!!)

        /* 然後取得 dataSource*/
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        /* 建造一個 Factory 傳入上面兩個參數 */
        val viewModelFactory = SleepQualityViewModelFactory(arguments.sleepNightKey, dataSource)

        /* 用新的 Factory 去獲得 ViewModel */
        val sleepQualityViewModel = ViewModelProvider(
            this, viewModelFactory).get(SleepQualityViewModel::class.java)

        /* 將 ViewModel 加到 DataBinding */
        binding.sleepQualityViewModel = sleepQualityViewModel

        /* 加入觀察者 */
        sleepQualityViewModel.navigateToSleepTracker.observe(this, Observer {
            if (it == true) {
                this.findNavController().navigate(
                    SleepQualityFragmentDirections.actionSleepQualityFragmentToSleepTrackerFragment())
                sleepQualityViewModel.doneNavigation()
            }
        })



        return binding.root
    }
}
