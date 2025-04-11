package vn.linhpv.miniblogapp.cache.search

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchKeywordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keyword: SearchKeyword)

    @Query("SELECT * FROM search_keywords ORDER BY id DESC LIMIT 10")
    fun getAllKeywords(): Flow<List<SearchKeyword>>
}
