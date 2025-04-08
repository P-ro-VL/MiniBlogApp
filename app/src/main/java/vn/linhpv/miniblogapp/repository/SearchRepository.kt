package vn.linhpv.miniblogapp.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import vn.linhpv.miniblogapp.MiniApplication
import vn.linhpv.miniblogapp.cache.search.SearchKeyword
import vn.linhpv.miniblogapp.cache.search.SearchKeywordDao

@Module
@InstallIn(SingletonComponent::class)
class SearchRepository {
    fun getDao() : SearchKeywordDao {
        return MiniApplication.instance.database.searchKeywordDao()
    }

    suspend fun saveKeyword(keyword: String) {
        getDao().insert(SearchKeyword(keyword = keyword))
    }

    fun getAllKeywords(): Flow<List<SearchKeyword>> = getDao().getAllKeywords()

    @Provides
    fun provides(): SearchRepository {
        return SearchRepository()
    }
}
