package com.example.nestrecyclerview.demo2.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.nestrecyclerview.demo2.R
import com.example.nestrecyclerview.demo2.bean.MainTabBean

class TabAdapter : BaseQuickAdapter<MainTabBean, BaseViewHolder>(R.layout.holder_tab) {
    override fun convert(holder: BaseViewHolder, item: MainTabBean) {
        val text=if(item.isSelected) "${item.text}\n选中" else item.text
        holder.setText(R.id.txt, text)
    }

    fun setSelectAt(index:Int){
        data.forEach { it.isSelected=false }
        data[index].isSelected=true
        setList(data)
    }
}