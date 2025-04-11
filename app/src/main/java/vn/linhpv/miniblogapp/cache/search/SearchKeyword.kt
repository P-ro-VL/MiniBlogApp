package vn.linhpv.miniblogapp.cache.search

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_keywords")
data class SearchKeyword(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val keyword: String
)
