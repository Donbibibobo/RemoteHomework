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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/*
<一> 告訴數據庫用什麼 entities
創建一個「擴展」RoomDataBase 的抽象類別，並用「@Database」註釋此類別，
剛註釋完是紅線，因為還要聲明 Database 的「entities」和「版本」，使用
(entities = [SleepNight::class], version = 1, exportSchema = false)
我們只有一個 table 所以提供 SleepNight::class，如果有更多可以加到這個 list 中
，該版從 1 開始，每當我們更換「Schema 架構」時，都必須增加版本號，如果忘記的話
App 就不會運作，「exportSchema 導出架構」默認是 true，會將數據庫的架構保存到文件中
，他為你提供了數據庫的版本歷史紀錄，對經常更改的覆率據庫很有幫助，但在此我們不需要所以
設置 false

<二> 告訴數據庫這個 entities 的 Dao，以便與數據庫溝通
聲明 Dao 類型的抽象值 sleepDatabaseDao，在此 App 中我們只有一張 table 和一個 Dao，
當然也可以多個 table 和 Dao，接下來定第一個「companion object 變身」，該變身允許別人
不用「instantiation 建構」這個類別就可以訪問此方法，來創建或是獲取 Database，由於此類別
的唯一目的是為我們提供數據庫，因此沒必要去「instantiation 建構」他

<三> 告訴數據庫得到的 Database 放哪
在 companion object 中，為 Database 聲明一個 var 變數，並初始化為 null，一但擁有
Database，INSTANCE 變數就會幫我們保存它，這將我們避免重複打開與 Database 的連接（
因為很貴），「@Volatile」幫助我們確保 INSTANCE 的值始終是最新的，並與所有的執行緒相同，
「Volatile 變數」的值不會被緩存，所有的寫入和讀取都會在主記憶體中進行，這表示當一個執行緒
改變了 INSTANCE，其他執行緒馬上就會知道，所以不會有像是「兩個執行緒同時在緩存更新 entity
然後彼此都不知道」的情況

<四>
接下來定義一個 getInstance() 方法，會回傳一個 reference 到 SleepDatabase，我們將使用
Database builder，但他需要一個 context 參數，所以我們要把 context 參數傳進來，然後我們
希望他回傳 SleepDatabase，在此方法中，我們要加一個 synchronized() 區域，多個執行緒有可能
會同時要求 database instance，使我們有兩個而不是一個，在此 App 中不太可能，但在更複雜的
App 中是有可能的，將程式碼包在 synchronized() 區塊表示「一次只有一個執行緒可以進入這塊代碼」
，藉此確保數據庫只初始化一次，我們用 this 把自己傳入 synchronized，所以我就可以訪問 context

<五>
在 synchronized 區塊中，我們將現有的 INSTANCE 值傳入 local variable instance 中，利用
 kotlin 的自動型轉來確保每次都回傳 SleepDataBase，且自動型轉只能用在 local variable 不能
用在 class variables，「return instance」這邊會有錯誤因為會返回 null，但是完成後就不會是
null 了。創建完 local variable 後，在回傳之前，可以用 if 來確認他是否已經是 Database 了，
在 if 中我們需要創建一個 Database，這邊使用「Room's Database Builder」

<六>
這邊我們調用 Room.databaseBuilder()，並提供上面傳入的 context，然後為了告訴他要建構哪個
數據庫所以傳入對 SleepDatabase 的引用，最後給這個 Database 一個名稱。下一步是「migration
 遷移」，通常在創建數據庫時必須提供一個具有「migration strategy」的「migration object」，
遷移的意思是如果我們「改變了數據庫的 schema (模式)」，例如說「改變 column 的類型或數量」，
我們需要一種方法將現有的表和數據庫轉換為新模式，「migration object」是一個定義「如何把舊架構
中的所有 row 轉換為新架構中的 row」的物件，因此如果用戶將一個「具有舊數據架構的舊版 App」更新
成「具有新數據架構的新版 App」時，睡眠的數據將不會消失，但因為現階段我們沒有使用者，然後定義
migration 已超出本課架構，所以這邊會直接刪除並重建數據庫而不是遷移，最為最後一步就可以使用
.build() 來建構數據庫了，然後將新的 Database 賦予 INSTANCE
 */

// 段落一
@Database(entities = [SleepNight::class], version = 2, exportSchema = false)
abstract class SleepDatabase(): RoomDatabase() {
// 段落二
    abstract val sleepDatabaseDao: SleepDatabaseDao

    companion object{
//段落三
        @Volatile
        private var INSTANCE: SleepDatabase? = null
// 段落四
        fun getInstance(context: Context): SleepDatabase {
            synchronized(this) {
// 段落五
                var instance = INSTANCE
                if (instance == null) {
// 段落六
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SleepDatabase::class.java,
                        "sleep_history_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}