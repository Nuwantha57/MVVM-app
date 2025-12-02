package com.eyepax.mvvm_app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eyepax.mvvm_app.model.Country
import com.eyepax.mvvm_app.model.BleDevice
//import com.eyepax.mvvm_app.model.WiFiNetwork

@Database(
    entities = [
        Country::class,
        BleDevice::class,      // ADD THIS
//        WiFiNetwork::class     // ADD THIS
    ],
    version = 2,  // INCREMENT VERSION
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun countryDao(): CountryDao
    abstract fun bleDeviceDao(): BleDeviceDao      // ADD THIS
//    abstract fun wifiNetworkDao(): WiFiNetworkDao  // ADD THIS

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mvvm_app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
