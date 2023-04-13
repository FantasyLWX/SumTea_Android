package com.sum.user.collection

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sum.common.model.ArticleInfo
import com.sum.common.provider.LoginServiceProvider
import com.sum.framework.toast.TipsToast
import com.sum.network.callback.IApiErrorCallback
import com.sum.network.flow.requestFlow
import com.sum.network.manager.ApiManager
import com.sum.network.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

/**
 * @author mingyan.su
 * @date   2023/3/24 18:29
 * @desc   我的收藏
 */
class MyCollectViewModel : BaseViewModel() {
    var collectListLiveData = MutableLiveData<MutableList<ArticleInfo>?>()
    val collectLiveData: MutableLiveData<Boolean?> = MutableLiveData()

    /**
     * 我的收藏列表
     * @param page  页码
     */
    fun getMyCollectList(page: Int) {
//        launchUIWithResult(errorCall = object : IApiErrorCallback {
//            override fun onError(code: Int?, error: String?) {
//                TipsToast.showTips(error)
//                collectListLiveData.value = null
//            }
//        }, responseBlock = {
//            ApiManager.api.getCollectList(page)
//        }) {
//            collectListLiveData.value = it?.datas
//        }

        viewModelScope.launch {
            val data = requestFlow(requestCall = {
                ApiManager.api.getCollectList(page)
            }, errorBlock = { code, error ->
                TipsToast.showTips(error)
                collectListLiveData.value = null
            })
            collectListLiveData.value = data?.datas
        }
    }

    /**
     * 收藏站内文章
     * @param id  文章id
     * @param originId 收藏之前的那篇文章本身的id
     */
    fun collectArticle(context: Context, id: Int, originId: Int, showLoading: (Boolean) -> Unit): LiveData<Boolean?> {
//        launchUIWithResult(responseBlock = {
//            ApiManager.api.cancelMyCollect(id, originId)
//        }, errorCall = object : IApiErrorCallback {
//            override fun onError(code: Int?, error: String?) {
//                super.onError(code, error)
//                collectLiveData.value = null
//            }
//
//            override fun onLoginFail(code: Int?, error: String?) {
//                super.onLoginFail(code, error)
//                collectLiveData.value = null
//                LoginServiceProvider.login(context)
//            }
//        }) {
//            collectLiveData.value = true
//        }
//        return collectLiveData

        launchFlow(errorCall = object : IApiErrorCallback {
            override fun onError(code: Int?, error: String?) {
                super.onError(code, error)
                collectLiveData.value = null
            }

            override fun onLoginFail(code: Int?, error: String?) {
                super.onLoginFail(code, error)
                collectLiveData.value = null
                LoginServiceProvider.login(context)
            }
        }, requestCall = {
            ApiManager.api.cancelMyCollect(id, originId)
        }, showLoading = showLoading) {
            collectLiveData.value = true
        }
        return collectLiveData
    }
}