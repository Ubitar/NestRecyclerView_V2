package com.example.nestrecyclerview.demo2.bean

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.example.nestrecyclerview.demo2.adapter.SubAdapter
import com.example.nestrecyclerview.demo2.adapter.TabAdapter

class MainListBean private constructor(override val itemType: Int) : MultiItemEntity {

    lateinit var text: String
    lateinit var tabAdapter: TabAdapter
    lateinit var subAdapters: MutableList<SubAdapter>

    constructor(itemType: Int, text: String) : this(itemType) {
        this.text = text
    }

    constructor(itemType: Int, tabAdapter: TabAdapter,subAdapters: MutableList<SubAdapter>) : this(itemType) {
        this.tabAdapter = tabAdapter
        this.subAdapters=subAdapters
    }

    companion object {
        const val TYPE_MAIN = 1
        const val TYPE_SUB = 2
    }

}