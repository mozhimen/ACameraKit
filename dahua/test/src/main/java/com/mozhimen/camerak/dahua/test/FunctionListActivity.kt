package com.mozhimen.camerak.dahua.test

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mozhimen.bindk.bases.viewdatabinding.activity.BaseActivityVDB
import com.mozhimen.camerak.dahua.test.databinding.ActivityFunctionListBinding
import com.mozhimen.camerak.dahua.test.databinding.ItemFuncBinding
import com.mozhimen.kotlin.utilk.android.content.startContext
import com.mozhimen.kotlin.utilk.wrapper.UtilKRes
import com.mozhimen.xmlk.recyclerk.quick.RecyclerKQuickAdapterVDB
import com.mozhimen.xmlk.vhk.VHKRecyclerVDB

class FunctionListActivity : BaseActivityVDB<ActivityFunctionListBinding>() {
    private val _funcList = mutableListOf(
        FuncBean(UtilKRes.gainString(R.string.activity_function_list_live_preview)) {
            startContext<PreviewActivity>()
        }
    )

    override fun initView(savedInstanceState: Bundle?) {
        vdb.recyclerFunc.layoutManager = LinearLayoutManager(this)
        vdb.recyclerFunc.adapter = RecyclerKQuickAdapterVDB<FuncBean, ItemFuncBinding>(
            _funcList,
            R.layout.item_func,
            BR.item_func
        ) { holder: VHKRecyclerVDB<ItemFuncBinding>, item: FuncBean, position: Int, currentSelectPos: Int ->
            holder.vdb.btnFunc.setOnClickListener {
                item.func.invoke()
            }
        }
    }

    data class FuncBean(
        val name: String,
        val func: () -> Unit
    )
}