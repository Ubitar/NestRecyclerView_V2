package com.example.nestrecyclerview.demo2.adapter

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.nestrecyclerview.demo2.R
import com.example.nestrecyclerview.demo2.bean.MainListBean
import com.example.nestrecyclerview.demo2.ry.NestChildRecyclerView
import com.example.nestrecyclerview.demo2.ry.NestRecyclerView

class MainAdapter : BaseMultiItemQuickAdapter<MainListBean, BaseViewHolder>(null),
    NestRecyclerView.INestAdapter<MainListBean> {

    private var viewPagerAdapter: VViewPagerAdapter? = null

    private var mCurPageIndex = 0

    private var onTabSelectListener: ((viewPager: ViewPager, position: Int) -> Unit)? = null
    private var onTurnPageListener: ((tabRecyclerView: RecyclerView, position: Int) -> Unit)? = null
    private var onChildScrollEndListener: ((recyclerView: NestChildRecyclerView, newState: Int) -> Unit)? =
        null

    init {
        addItemType(MainListBean.TYPE_MAIN, R.layout.holder_item1)
        addItemType(MainListBean.TYPE_SUB, R.layout.holder_item2)
    }

    override fun convert(holder: BaseViewHolder, item: MainListBean) {
        when (item.itemType) {
            MainListBean.TYPE_MAIN -> convertMainItem(holder, item)
            MainListBean.TYPE_SUB -> convertSub(holder, item)
        }
    }

    private fun convertMainItem(holder: BaseViewHolder, item: MainListBean) {
        holder.setText(R.id.txt, item.text)
    }

    private fun convertSub(holder: BaseViewHolder, item: MainListBean) {
        val tabLayout = holder.getView<RecyclerView>(R.id.tabLayout)
        val viewPager = holder.getView<ViewPager>(R.id.viewPager)

        item.tabAdapter.setOnItemClickListener { adapter2, view, position ->
            onTabSelectListener?.invoke(viewPager, position)
        }
        tabLayout.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        tabLayout.itemAnimator = null
        tabLayout.adapter = item.tabAdapter


        val viewList = ArrayList<View>(item.subAdapters.size)
        item.subAdapters.forEachIndexed { index, mainSecondAdapter ->
            val recyclerView = createChildRecyclerView(item, index)
            recyclerView.adapter = mainSecondAdapter
            viewList.add(recyclerView)
        }

        val pageChangeListener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(i: Int) {
                item.tabAdapter.setSelectAt(i)
                mCurPageIndex = i
                onTurnPageListener?.invoke(tabLayout, mCurPageIndex)
            }
        }
        if (viewPagerAdapter == null) {
            viewPagerAdapter = VViewPagerAdapter(null, viewList)
            viewPager.addOnPageChangeListener(pageChangeListener)
        } else viewPagerAdapter?.viewList = viewList
        viewPager.pageMargin = 10
        viewPager.adapter = viewPagerAdapter

        /** 触发选择了当前片段的事件无论该片段是否被初始化过 ，要自己在adapter外自行判断是否已初始化 */
        pageChangeListener.onPageSelected(mCurPageIndex)

    }

    /** 获得当前子RecyclerView组件 */
    override fun getCurrentChildRecyclerView(): NestChildRecyclerView? {
        if (viewPagerAdapter == null) return null
        return viewPagerAdapter?.viewList?.get(mCurPageIndex) as NestChildRecyclerView
    }

    /** 创建子NestRecyclerView组件 */
    override fun createChildRecyclerView(item: MainListBean, index: Int): NestChildRecyclerView {
        val recyclerView = NestChildRecyclerView(context, null, 0)
        val columnCount = 1
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (getLastVisibleItem(recyclerView) >= getTotalItemCount(recyclerView) - 1)
                        onChildScrollEndListener?.invoke(
                            recyclerView as NestChildRecyclerView,
                            newState
                        )
                }
            }

            private fun getLastVisibleItem(childRecyclerView: RecyclerView): Int {
                val layoutManager = childRecyclerView.layoutManager

                var lastVisibleIndex = -1

                layoutManager.let {
                    if (it is StaggeredGridLayoutManager) {
                        if (columnCount > 1) {
                            val iArr = IntArray(2)
                            it.findLastVisibleItemPositions(iArr)
                            lastVisibleIndex = if (iArr[0] > iArr[1]) iArr[0] else iArr[1]
                        } else {
                            val iArr = IntArray(1)
                            it.findLastVisibleItemPositions(iArr)
                            lastVisibleIndex = iArr[0]
                        }
                    } else if (it is LinearLayoutManager) {
                        lastVisibleIndex = it.findLastVisibleItemPosition()
                    } else if (it is GridLayoutManager) {
                        lastVisibleIndex = it.findLastVisibleItemPosition()
                    }
                }

                return lastVisibleIndex
            }

            fun getTotalItemCount(childRecyclerView: RecyclerView): Int {
                return childRecyclerView.adapter?.itemCount ?: -1
            }

        })
        return recyclerView
    }


    override fun setList(list: Collection<MainListBean>?) {
        //在刷新列表数据时将索引重新设置为0
        mCurPageIndex = 0
        super.setList(list)
    }

    fun setTabSelectListener(listener: ((viewPager: ViewPager, position: Int) -> Unit)) {
        this.onTabSelectListener = listener
    }

    fun setOnTurnPageListener(listener: (tabRecyclerView: RecyclerView, position: Int) -> Unit) {
        this.onTurnPageListener = listener
    }

    fun setOnChildScrollEndListener(listener: ((recyclerView: NestChildRecyclerView, newState: Int) -> Unit)? = null) {
        this.onChildScrollEndListener = listener
    }


}