package com.example.nestrecyclerview.demo2.ry

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration

/**
 * 内层的RecyclerView
 */
class NestChildRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseRecyclerView(context, attrs, defStyleAttr) {

    private var parentRecyclerView: NestRecyclerView? = null

    /**
     * 触发移动事件的最小距离
     */
    private val mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

    /**
     *  触摸按下时的X、Y值
     */
    private var touchDownX: Float = 0f
    private var touchDownY: Float = 0f

    /**
     * 当前状态
     */
    private var state: Int = DRAG_IDLE

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        // 是否已经停止scrolling
        if (state == SCROLL_STATE_IDLE) {
            // 这里是考虑到当整个childRecyclerView被detach之后，及时上报parentRecyclerView
            val velocityY = getVelocityY()
            if (velocityY < 0 && computeVerticalScrollOffset() == 0) {
                parentRecyclerView?.fling(0, velocityY)
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            // ACTION_DOWN 触摸按下，保存临时变量
            state = DRAG_IDLE
            touchDownX = ev.rawX
            touchDownY = ev.rawY
            this.stopFling()
        }
        return super.onInterceptTouchEvent(ev)
    }

    /**
     * 这段逻辑主要是RecyclerView最底部，垂直上拉后居然还能左右滑动，不能忍
     */
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_MOVE) {
            // ACTION_MOVE 判定垂直还是水平滑动
            if (state == DRAG_IDLE) {
                val xDistance = Math.abs(ev.rawX - touchDownX)
                val yDistance = Math.abs(ev.rawY - touchDownY)

                if (xDistance > yDistance && xDistance > mTouchSlop) {
                    // 水平滑动
                    state = DRAG_HORIZONTAL
                } else if (yDistance > xDistance && yDistance > mTouchSlop) {
                    // 垂直滑动
                    state = DRAG_VERTICAL
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
        }
        return super.onTouchEvent(ev)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        searchAndBindNestRecyclerView()
    }

    /**
     * 与NestRecyclerView父组件绑定
     */
    private fun searchAndBindNestRecyclerView() {
        var lastTraverseView: View = this
        var parentView = this.parent as View?
        while (parentView != null) {
            if (parentView is NestRecyclerView) {
                // 碰到ParentRecyclerView，设置结束
                parentView.setChildPagerContainer(lastTraverseView)
                this.parentRecyclerView = parentView
                return
            }

            lastTraverseView = parentView
            parentView = parentView.parent as View
        }
    }

    companion object {
        private const val DRAG_IDLE = 0
        private const val DRAG_VERTICAL = 1
        private const val DRAG_HORIZONTAL = 2
    }

}