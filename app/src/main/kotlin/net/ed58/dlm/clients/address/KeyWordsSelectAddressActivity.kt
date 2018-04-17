package net.ed58.dlm.clients.address

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.wise.common.baseapp.AppManager
import kotlinx.android.synthetic.main.activity_key_words_select_address.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.adapter.KeyWordsPoiSearchAdapter
import net.ed58.dlm.clients.base.BaseCoreActivity
import net.ed58.dlm.clients.global.Constant

class KeyWordsSelectAddressActivity : BaseCoreActivity(), PoiSearch.OnPoiSearchListener {

    var type:Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    private fun initView() {
        type = intent.getIntExtra("type",-1)


        edit_search.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchPoiItem(charSequence.toString())
            }

        })
        image_back.setOnClickListener {
            AppManager.getAppManager().finishActivity()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_key_words_select_address
    }


    fun searchPoiItem(keyWords:String) {
        val query = PoiSearch.Query(keyWords, "", "021")
        query.pageSize = 20
        query.pageNum = 0
        val poiSearch = PoiSearch(this, query)
        poiSearch.setOnPoiSearchListener(this)
        poiSearch.searchPOIAsyn()
    }


    override fun onPoiItemSearched(poiItem: PoiItem?, p1: Int) {

    }

    override fun onPoiSearched(poiResult: PoiResult?, p1: Int) {
        if (poiResult != null) {
            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            val keyWordsPoiSearchAdapter = KeyWordsPoiSearchAdapter(this, poiResult.pois)
            recyclerView.adapter = keyWordsPoiSearchAdapter
        }

    }



    companion object {
        fun startActivityForResult(context: Activity) {
            val intent = Intent(context, KeyWordsSelectAddressActivity::class.java)
            context.startActivityForResult(intent, Constant.REQUEST_FIRST_CODE)
        }
    }
}
