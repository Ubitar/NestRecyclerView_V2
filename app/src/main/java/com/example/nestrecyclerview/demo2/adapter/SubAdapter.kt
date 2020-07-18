package com.example.nestrecyclerview.demo2.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.nestrecyclerview.demo2.R

class SubAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.holder_item3) {
    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.txt, item)
    }
}