package vn.linhpv.miniblogapp.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import vn.linhpv.miniblogapp.cache.post.FavoritePost
import vn.linhpv.miniblogapp.cache.post.FavoritePostDao
import vn.linhpv.miniblogapp.cache.search.SearchKeyword
import vn.linhpv.miniblogapp.cache.search.SearchKeywordDao

@Database(entities = [FavoritePost::class, SearchKeyword::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritePostDao(): FavoritePostDao
    abstract fun searchKeywordDao(): SearchKeywordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mini_post_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
