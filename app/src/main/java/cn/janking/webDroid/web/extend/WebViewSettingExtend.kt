package cn.janking.webDroid.web.extend

import android.os.Build
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import cn.janking.webDroid.constant.PathConstants
import cn.janking.webDroid.constant.WebConstants
import cn.janking.webDroid.util.LogUtils
import cn.janking.webDroid.util.ProcessUtils
import cn.janking.webDroid.util.WebUtils
import cn.janking.webDroid.web.WebConfig

/**
 * @author Janking
 */
fun WebView.defaultSetting() {
    settings.apply {
        setJavaScriptEnabled(true)
        //支持缩放
        setSupportZoom(true)
        //解决 sysu.edu.cn 超出屏幕部分内容不加载，只有背景的问题
        useWideViewPort = true
        //解决 sysu.edu.cn 不能缩放的问题
        builtInZoomControls = true
        //隐藏原生的缩放按钮
        displayZoomControls = false;
        if (WebUtils.checkNetwork(this@defaultSetting.context)) { //根据cache-control获取数据。
            cacheMode = WebSettings.LOAD_DEFAULT
        } else { //没网，则从本地获取，即离线加载
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //适配5.0不允许http和https混合使用情况
            setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW)
            this@defaultSetting.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
        textZoom = 100
        databaseEnabled = true
        setAppCacheEnabled(true)
        loadsImagesAutomatically = true
        setSupportMultipleWindows(false)
        // 是否阻塞加载网络图片  协议http or https
        blockNetworkImage = false
        // 允许加载本地文件html  file协议
        allowFileAccess = true
        // 通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
        allowFileAccessFromFileURLs = false
        // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
        allowUniversalAccessFromFileURLs = false
        javaScriptCanOpenWindowsAutomatically = true
        layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        loadWithOverviewMode = false
        domStorageEnabled = true
        setNeedInitialFocus(true)
        //设置编码格式
        defaultTextEncodingName = "utf-8"
        defaultFontSize = 16
        //设置 WebView 支持的最小字体大小，默认为 8
        minimumFontSize = 12
        setGeolocationEnabled(true)
        val dir = PathConstants.dirWebCache
        if(WebConfig.DEBUG){
            LogUtils.i(
                "WebView.defaultSetting()",
                "dir:" + dir + "   appcache:" + PathConstants.dirWebCache
            )
        }
        setAppCachePath(dir)
        userAgentString +=  WebConstants.USERAGENT_UC
        if(WebConfig.DEBUG){
            LogUtils.i(
                "WebView.defaultSetting()",
                "UserAgentString : $userAgentString"
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // 安卓9.0后不允许多进程使用同一个数据目录，需设置前缀来区分
// 参阅 https://blog.csdn.net/lvshuchangyin/article/details/89446629
            val context = this@defaultSetting.context
            val processName = ProcessUtils.getCurrentProcessName()
            if (context.applicationContext.packageName != processName) {
                WebView.setDataDirectorySuffix(processName)
            }
        }
    }

}