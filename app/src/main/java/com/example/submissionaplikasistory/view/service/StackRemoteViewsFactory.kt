package com.example.submissionaplikasistory.view.service

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import androidx.paging.PagingSource
import com.bumptech.glide.Glide
import com.example.submissionaplikasistory.R
import com.example.submissionaplikasistory.datasource.local.DaoService
import com.example.submissionaplikasistory.datasource.local.DaoStoryConfig
import com.example.submissionaplikasistory.datasource.local.EntityDaoStory
import com.example.submissionaplikasistory.view.PostWidget
import kotlinx.coroutines.runBlocking

internal class StackRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    private var currentList = listOf<EntityDaoStory>()
    private lateinit var connection: DaoService

    override fun onCreate() {
        connection = DaoStoryConfig.getInstance(context).getService()
    }

    override fun onDataSetChanged() {
        fetchData()
    }

    fun fetchData() {
        runBlocking {
            currentList = connection.getStoryListEntityDaoStory()
            println(currentList)
        }
    }

    override fun onDestroy() {

    }

    override fun getCount() = currentList.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.item_stack_widget)
        try {
            val bitmap = Glide.with(context)
                .asBitmap()
                .load(currentList[position].photoUrl)
                .submit()
                .get()
            rv.setImageViewBitmap(R.id.widget_iamgeView, bitmap)
        } catch (e: Exception) {
            println(e)
        }

        val extras = bundleOf(
            PostWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.widget_iamgeView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}