# 简介及使用

## **Android Debug接口调试工具，轻便简约**

**硬性要求：必须支持okhttp**

**最新版本：1.1.6**

**效果如图**

![apiLog](https://github.com/Potato-2020/apiLog/blob/master/apiLog.gif)



## **使用规则**

### a、引入依赖

在app下的build.gradle中

```groovy
dependencies {
    //Debug调试界面工具
	implementation 'com.github.Potato-2020:apiLog:1.1.6'
	//依赖编译工具
	annotationProcessor 'com.github.Potato-2020:apiLogCompiler:1.1.6'
}
```



### b、代码中的使用


**添加网络拦截器**

```java
new OkHttpClient.Builder().addNetworkInterceptor(new ApiLogInterceptor(mContext, "端口号"));
```

加入网络拦截器后，访问接口后，会将数据存储到本地数据库，方便查新接口详情



**在MainActivity中的代码**

在MainActivity中，摇一摇（三下，频率不要太快），或者翻一番（正、反、正，频率不要太快）

就会进入到DebugActivity了。

```kotlin
class MainActivity : AppCompatActivity(), DebugManager.DebugListener {
    private var debugManager : DebugManager? = null
    private var receiverPotato: ReceiverPotato? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //动态注册广播
        val intentFilter = IntentFilter()
        intentFilter.addAction(ReceiverPotato.ACTION)
        receiverPotato = object : ReceiverPotato() {
            override fun changeBaseUrl(baseUrl: String?) {
                //修改了接口的host，自己处理逻辑（比如切换网络接口环境）
                Log.e("Potato>>>baseUrl>>>", baseUrl)
            }

            override fun changeImageUrl(imageUrl: String?) {
                //修改了图片url的host，自己处理逻辑
                Log.e("Potato>>>imageUrl>>>", imageUrl)
            }

            override fun openWebView(h5: String?) {
                //跳转webview
                Log.e("Potato>>>h5>>>", h5)
            }
        }
        registerReceiver(receiverPotato, intentFilter)
        if (debugManager == null) debugManager = DebugManager()
        if (debugManager != null) debugManager?.setListener(this, this)
    }

    override fun onResume() {
        super.onResume()
        if (debugManager != null) debugManager?.onResume()
    }

    override fun onPause() {
        super.onPause()
        if(debugManager != null) debugManager?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        //解注册
        unregisterReceiver(receiverPotato)
        if(debugManager != null) debugManager?.onDestory()
    }

    override fun debugApiLog() {
        //跳转到Debug调试界面
        debugManager?.openDebug(
            this,
            "https://www.baidu.com",//接口的host
            "https://www.ailiuynos.cn",//图片url的host
            "1.0.0");//app版本号
    }
}
```

