package com.example.submissionaplikasistory.view.viewmodel

import android.annotation.SuppressLint
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.submissionaplikasistory.DataDummy
import com.example.submissionaplikasistory.LiveDataTestUtil.getOrAwaitValue
import com.example.submissionaplikasistory.MainDispatcherRule
import com.example.submissionaplikasistory.datasource.local.EntityDaoStory
import com.example.submissionaplikasistory.repository.StoryRepository
import com.example.submissionaplikasistory.view.adapter.StoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import kotlin.math.exp

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()
    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test()
    fun `not null when get story and return the output`() = runTest {
        val getDummyEntity = DataDummy.generateEntityResponse()
        val data = QuotePagingSource.snapshot(getDummyEntity)
        val expected = MutableLiveData<PagingData<EntityDaoStory>>()
        val token = mutableMapOf(Pair("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLTY4XzZWLXpDT3ctYmgyUHIiLCJpYXQiOjE3MTQxMTE2NzN9.0khGdVcWtpI8ZGSNpMc2_RKee6c8q5fHR_RSKd4iLe0"))
        expected.value = data
        Mockito.`when`(storyRepository.getStoryFromDatabaseDao(token)).thenReturn(expected)

        val storyViewModel = StoryViewModel(storyRepository)
        val result : LiveData<PagingData<EntityDaoStory>>  = storyViewModel.getStoryDao(token)
        val resultPagingDataFromViewModel : PagingData<EntityDaoStory> = result.getOrAwaitValue()

        val dif = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.diffUtil,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        dif.submitData(resultPagingDataFromViewModel)

        assertNotNull(dif.snapshot())
        assertEquals(getDummyEntity.size, dif.snapshot().size)
        assertEquals(getDummyEntity[0], dif.snapshot()[0])
    }
    
    @Test()
    fun `should null when get story and return the output size 0`() = runTest {
        val data : PagingData<EntityDaoStory> = PagingData.from(emptyList())
        val expected = MutableLiveData<PagingData<EntityDaoStory>>()
        val token = mutableMapOf(Pair("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLTY4XzZWLXpDT3ctYmgyUHIiLCJpYXQiOjE3MTQxMTE2NzN9.0khGdVcWtpI8ZGSNpMc2_RKee6c8q5fHR_RSKd4iLe0"))
        expected.value = data
        Mockito.`when`(storyRepository.getStoryFromDatabaseDao(token)).thenReturn(expected)

        val storyViewModel = StoryViewModel(storyRepository)
        val result : LiveData<PagingData<EntityDaoStory>>  = storyViewModel.getStoryDao(token)
        val resultPagingDataFromViewModel : PagingData<EntityDaoStory> = result.getOrAwaitValue()

        val dif = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.diffUtil,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        dif.submitData(resultPagingDataFromViewModel)
        
        assertEquals(0, dif.snapshot().size)
    }
}

class QuotePagingSource : PagingSource<Int, LiveData<List<EntityDaoStory>>>() {
    companion object {
        fun snapshot(items: List<EntityDaoStory>): PagingData<EntityDaoStory> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<EntityDaoStory>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<EntityDaoStory>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}