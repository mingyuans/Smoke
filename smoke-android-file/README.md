## 简介
smoke-android-file 模块提供日志写入到文件的功能,该模块基于 [Wechart Mars](https://github.com/Tencent/mars)  开发;

Mars 中的 Xlog 模块因为依赖庞大,实际使用中稍有些不便,这里做了依赖剥离,简化工程代码,另外也是针对 Android 平台做了接口和参数的调整;

相关技术原理见 [Wechart Mars Xlog 文档](http://mp.weixin.qq.com/s/cnhuEodJGIbdodh0IxNeXQ)


## 特性
* 使用 MMAP 做日志缓冲,实现 App 异常时不丢日志;
* 实现日志压缩功能
* ~~提供日志加密功能~~(未实现)

## 使用
### 依赖配置
```
compile 'com.mingyuans.android:smoke-android-file:2.1.2'

//AAR 带有全版本的SO库,文件较大,如果只需要特定的版本,需要添加过滤
android {
  defaultConfig {
    ndk {
      abiFilters 'armeabi', 'armeabi-v7a'
    }
  }
}
```
### 初始化&使用
```java
Processes processes = AndroidProcesses.androidDefault();
processes.addPrinter(new AndroidFilePrinter());
Smoke.install("tag",processes);

Smoke.info("Hello,mars!");
```

### 日志解码
```python
python decode_mars_log_file.py xxxx.sm

or

python decode_mars_log_file.py sm_dir_path
```