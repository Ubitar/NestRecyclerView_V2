package com.example.nestrecyclerview.demo2.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import java.util.*

class VViewPagerAdapter(
        var mTitles: ArrayList<String>? = null,
        var viewList: ArrayList<View>
) : PagerAdapter() {

    override fun getCount(): Int {
        return viewList.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = viewList[position]
        if (container == view.parent) {
            container.removeView(view)
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        //container.removeView((View) object);
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if (mTitles == null) return ""
        return mTitles!![position]
    }

}