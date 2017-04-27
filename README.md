[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)[![Build Status](https://travis-ci.org/mingyuans/Smoke.svg?branch=master)](https://travis-ci.org/mingyuans/Smoke)[![Download](https://api.bintray.com/packages/mingyuan/maven/smoke/images/download.svg) ](https://bintray.com/mingyuan/maven/smoke/_latestVersion)

## 简介
Smoke 是一个在 Java 和 Android 平台上使用的日志封装库，具备以下特性：
* 接口使用简洁,支持 无TAG 打印;
* 支持日志格式化时自动识别数组/列表/JSON/XML等类型或是格式;
* 支持长日志打印,Android 平台下不受系统 Logcat 长度限制;
* 支持多级 TAG 和日志配置继承;
* 支持日志快速跳转到代码位置;
* 使用链式调用,自定义日志修饰/打印功能强大;
* 支持日志按级别过滤;
* 支持SDK异常后自动关闭自身功能;
* 支持日志文件记录

## 编译配置
```
compile 'com.mingyuans.android:smoke-java:2.1.3'
compile 'com.mingyuans.android:smoke-android:2.1.1'
```

## 初始化&使用
### Java 平台
```
Smoke.install("Smoke",null);

Smoke.debug("Hello,Smoke!");
```

### Android 平台
```
Smoke.install("Smoke",AndroidProcesses.androidDefault());

Smoke.debug("Hello,Smoke!");
```

### Android 平台下日志文件记录
Android 平台下支持日志写入到文件中保存,详见:
[smoke-android-file: README](https://github.com/mingyuans/Smoke/tree/master/smoke-android-file)

## 新特性和使用示例
### 接口简洁
> 相比系统原生的 Log.d(TAG,"message") 打印，Smoke 直接使用默认方法名作为TAG,使开发者专注于日志消息本身；
> 传统的String.format形式，使得我们打印一个消息时往往还要去注意参数的类型，十分讨厌; 为了更懒一点，Smoke 内部自动做了参数的类型判断，并支持 MessageFormat 格式化形式；

#### 支持空消息打印，常用于标识是否有某个逻辑方法是否有调用到；
```
public void doSmokePrint() {
    Smoke.verbose();
}
//Output:
╔════════════════════════════════════════════════════════════════════════════════════════
║[SmokeTest.doSmokePrint(SmokeTest.java:59)][thread: main]
╟────────────────────────────────────────────────────────────────────────────────────────
║
╚════════════════════════════════════════════════════════════════════════════════════════
```

#### 支持标准的日志格式化；
```
public void doSmokePrint() {
    Smoke.debug("Hello，%s","Lilei");
}
//Output:
╔════════════════════════════════════════════════════════════════════════════════════════
║[SmokeTest.doSmokePrint(SmokeTest.java:59)][thread: main]
╟────────────────────────────────────────────────────────────────────────────────────────
║Hello,Lilei
╚════════════════════════════════════════════════════════════════════════════════════════
```

#### 支持且推荐使用MessageFormat形式的日志格式化；
```
public void doSmokePrint() {
    Smoke.debug("Hello，{}","Hanmeimei");
}
//Output:
╔════════════════════════════════════════════════════════════════════════════════════════
║[SmokeTest.doSmokePrint(SmokeTest.java:59)][thread: main]
╟────────────────────────────────────────────────────────────────────────────────────────
║Hello,Hanmeimei
╚════════════════════════════════════════════════════════════════════════════════════════
```
```
public void doSmokePrint() {
    Smoke.debug("Hello，{0} and {1}","Lilei","Hanmeimei");
}
//Output:
╔════════════════════════════════════════════════════════════════════════════════════════
║[SmokeTest.doSmokePrint(SmokeTest.java:59)][thread: main]
╟────────────────────────────────────────────────────────────────────────────────────────
║Hello,Lilei and Hanmeimei
╚════════════════════════════════════════════════════════════════════════════════════════
```

#### 支持XML/JSON自动判断并格式化打印
```
public void doSmokePrint() {
    String xmlString = "<team><member name=\"Elvis\"/><member name=\"Leon\"/></team>";
    Smoke.info(xmlString);
}
//Output:
╔════════════════════════════════════════════════════════════════════════════════════════
║[SmokeTest.doSmokePrint(SmokeTest.java:132)][thread: main]
╟────────────────────────────────────────────────────────────────────────────────────────
║<?xml version="1.0" encoding="UTF-8"?>
║<team>
║  <member name="Elvis"/>
║  <member name="Leon"/>
║</team>
╚════════════════════════════════════════════════════════════════════════════════════════
```
```
public void testJson() {
    String json = "{\"code\":\"1\",\"content\":\"hello\"}";
    Smoke.warn(json);
}
//Output:
╔════════════════════════════════════════════════════════════════════════════════════════
║[SmokeTest.testJson(SmokeTest.java:125)][thread: main]
╟────────────────────────────────────────────────────────────────────────────────────────
║{
║  "code": "1",
║  "content": "hello"
║}
╚════════════════════════════════════════════════════════════════════════════════════════
```

#### 支持数组直接打印
```
public void testFormat() {
    String[] array = new String[]{"Hello","World"};
    Smoke.debug("array is : {0}",(Object)array);
}
//Output:
╔════════════════════════════════════════════════════════════════════════════════════════
║[SmokeTest.testFormat(SmokeTest.java:101)][thread: main]
╟────────────────────────────────────────────────────────────────────────────────────────
║array is : [Hello,World]
╚════════════════════════════════════════════════════════════════════════════════════════
```
### 支持多级TAG 和 日志配置继承关系

>多级 TAG 用于帮助区分来着不同模块的日志消息；
>newSub 方法得到的子对象将继承父对象的日志配置；
```
public void testNewSub() {
    SubSmoke subSmoke = Smoke.newSub("subOne");
    subSmoke.debug("Hello, subOne!");
    SubSmoke subSmoke1 = subSmoke.newSub("subTwo");
    subSmoke1.debug("Hello, {0}!","subTwo");
}
//Output:
╔════════════════════════════════════════════════════════════════════════════════════════
║【subOne】[SmokeTest.testNewSub(SmokeTest.java:151)][thread: main]
╟────────────────────────────────────────────────────────────────────────────────────────
║Hello, subOne!
╚════════════════════════════════════════════════════════════════════════════════════════

╔════════════════════════════════════════════════════════════════════════════════════════
║【subOne|subTwo】[SmokeTest.testNewSub(SmokeTest.java:153)][thread: main]
╟────────────────────────────────────────────────────────────────────────────────────────
║Hello, subTwo!
╚════════════════════════════════════════════════════════════════════════════════════════

```

### 支持 AndroidStudio 中代码定位
点击日志中高亮的文字即可让 AndrodiStudio 跳转到该日志打印位置；
![image](http://ocrfgcvcm.bkt.clouddn.com/smoke_link_file.png)

### 支持日志等级过滤
```
Smoke.setLogPriority(Smoke.INFO);
```

### 支持日志流程自定义
>Smoke 使用链式递归调用的设计来处理日志的修饰和输出并开放了流程配置接口，开发者可以根据自己的需要进行日志处理流程的修改；
```
public void testProcessDIY() throws Exception {
   SubSmoke subSmoke = new SubSmoke(InstrumentationRegistry.getContext(),"Smoke");
   String drawBoxIdentify = Processes.getIdentify(DrawBoxProcess.class);
   subSmoke.getProcesses()
      .removeByIdentify(drawBoxIdentify) //If you do not like box!
      .addCollector(new MyDIYLogCollector())
      .addPrinter(new MyDIYLogPrinter());
   subSmoke.debug("Hello");
}

public static class MyDIYLogCollector extends Smoke.Process {
   @Override
   public boolean proceed(Smoke.LogBean logBean, List<String> messages, Chain chain) {
       LinkedList<String> newMessages = new LinkedList<>();
       for (String message: messages) {
       newMessages.add("Test" + message);
       }
       return chain.proceed(logBean,newMessages);
   }
}

public static class MyDIYLogPrinter extends Smoke.Process {

   @Override
   public boolean proceed(Smoke.LogBean logBean, List<String> messages, Chain chain) {
       for (String message: messages) {
       Log.i("SmokeDIY",message);
       }
       return chain.proceed(logBean,messages);
   }
}
```

>**想要日志特殊过滤？想要去除框框？想要加前缀？想要输出到其他控制台？
统统可以通过增删 Process 来实现！**


### 支持SDK场景运用下的日志控制
当我们在开发某个 SDK 时，不可避免的，我们会需要对外提供一个接口，让SDK 使用者可以控制我们 SDK 内的日志打印，方便进行调试；

Smoke 自带供外部设置的 Printer，
更进一步，如果调用方也使用 Smoke 模块，还可以实现 SDK 内 Smoke 日志实例挂载到对方日志实例上，实现日志输出格式的统一；

```

public void testAttachPrinter() throws Exception {
    SubSmoke subSmoke = new SubSmoke(InstrumentationRegistry.getContext(),"Smoke",null);
    subSmoke.attach(new Printer() {
    @Override
    public void println(int priority, String tag, String message) {
        Log.println(priority,tag,"Smoke Printer: Hello");
        Log.println(priority,tag,message);
    }
    });
    subSmoke.debug("Hello, Printer!");
}
```

```
public void testAttachSub() throws Exception {
    SubSmoke subSmoke = new SubSmoke(InstrumentationRegistry.getContext(),"SmokeParent",null);
    subSmoke.getProcesses().addCollector(new Smoke.Process() {
        @Override
        public boolean proceed(Smoke.LogBean logBean, List<String> messages, Chain chain) {
            messages.add(0,"Smoke A: Hello, Smoke B.");
            return chain.proceed(logBean,messages);
        }
    });
    SubSmoke subSmoke1 = new SubSmoke(InstrumentationRegistry.getContext(),"smokeChild",null);
    subSmoke1.attach(subSmoke);
    subSmoke1.info("Hello,Smoke.");
}
```

### 混淆后的 LOG 恢复
Smoke 使用类名和方法名作为 LOG 的 TAG，在 Proguard 混淆后，恢复 LOG 中的类名、方法名的方法如下:
1. 保存 LOG 到文件中;
2. Linux/Mac 下执行 smoke-java 工程下的 smoke_retrace.sh:
```shell
  ./smoke_retrace.sh mapping_file log_file
```
或是使用 Proguard 的 retrace.sh (win下为 retrace.bat）执行如下命令:
```
retrace -regex ".*\\[%c.%m\\]\\s*\\[.*\\]\\s*|(?:\\s*%c:.*)|(?:.*at\\s+%c.%m\\s*\\(.*?(?::%l)?\\)\\s*)" mapping_path log_path
```

### 接口和实现分离,降低 Bug 和 需求变更带来的修改成本
对于第三方的东西，总是要保持着怀疑和不信任去使用；
日志模块作为一个需要埋点到 App 中诸多地方的模块，一旦有 Bug 发生和后期更换需求，更换日志模块将是一个浩大的工程；

为了解决这个问题，Smoke 设计之时也尽力进行了接口和实现的分离,降低后期 Bug 或是需求变更的修改成本：
1. Smoke 库分 Smoke 和 SubSmoke 两部分，Smoke 本身为空接口类，SubSmoke 才是具体实现；
   对于 App 场景，开发者直接使用 Smoke 类来进行日志打印；至于其核心 SubSmoke 实例则可以随时更换；

2. SubSmoke 作为日志打印的具体实现，其内部本身也没有太多逻辑，其主要核心实现均依赖于多个 Process 的组合配置。
   如果实践中发现某个 Process 有Bug，开发者完全可以移除或修改该 Process，不需要等等作者修改发布新的版本；

综上两个方案，最大化降低 Smoke 因本身 Bug 或后期不适用需要大规模替换的情况和成本；

### 自带的小策略
Smoke 在前期测试有限的情况下难以保证稳定性；为了降低试错成本，Smoke自带了一个自身异常后自动禁用的小策略;

## Licence
```
Copyright 2016-2017 Mingyuans

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

```









