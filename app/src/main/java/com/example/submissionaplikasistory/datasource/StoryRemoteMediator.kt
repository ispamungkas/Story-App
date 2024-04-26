package com.example.submissionaplikasistory.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.submissionaplikasistory.datasource.api.ApiService
import com.example.submissionaplikasistory.datasource.local.DaoStoryConfig
import com.example.submissionaplikasistory.datasource.local.EntityDaoStory
import com.example.submissionaplikasistory.datasource.local.RemoteKeys
import com.example.submissionaplikasistory.utils.Utils

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val db : DaoStoryConfig,
    private val apiService: ApiService,
    private val token: Map<String, String>,
): RemoteMediator<Int, EntityDaoStory>() {

    private companion object {
        const val INITIAL_PAGE_VALUE = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EntityDaoStory>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH ->{
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_VALUE
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val response = apiService.getStories(token, page, state.config.pageSize)
            val endOfPagination = response.body()?.listStory?.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeyDao().delete()
                    db.getService().deleteAll()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPagination!!) null else page + 1
                val keys = response.body()?.listStory?.map {
                    RemoteKeys(id = it.id!!, prevKey = prevKey, nextKey = nextKey)
                }

                val body = Utils().convertResponseStoryEntityDaoStory(response.body())
                db.remoteKeyDao().insertAll(keys!!)
                db.getService().addStory(body)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPagination!!)
        } catch(e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, EntityDaoStory>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            db.remoteKeyDao().getRemoteById(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, EntityDaoStory>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            db.remoteKeyDao().getRemoteById(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, EntityDaoStory>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                db.remoteKeyDao().getRemoteById(id)
            }
        }
    }

}