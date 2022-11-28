# Android Studio 开发 xposed 模块

## 项目环境

|      name      | versions |
|:--------------:|:--------:|
| Android Studio | 2021.3.1 |
| Gradle Plugin  |  7.1.2   |
|     Gradle     |  7.5.1   |
|     xposed     |    82    |

## 新建项目

---
* 选择No Activity后点击NEXT
  ![img.png](../../images/soul-dream/img.png)
---
* 设置项目名称、包名、项目位置、开发语言、最小SDK，点击Finish
  ![img.png](../../images/soul-dream/img_1.png)
---
* 将项目视图切换为Project
  ![img.png](../../images/soul-dream/img_2.png)
---
* 删除无用的资源文件（长按Ctrl+鼠标左键将这些文件选中后，再按Delete键和Enter删除）
  ![img.png](../../images/soul-dream/img_3.png)
---
* 修改AndroidManifest.xml(安卓清单文件)
  ![img.png](../../images/soul-dream/img_6.png)
---
## 配置环境

### 编辑AndroidManifest.xml（将application单标签改为双标签，并加上xposed的meta-data标签）

```xml
    <!--删除其他多余的，只保留label-->
<application 
    android:label="@string/app_name">
    <!--模块标识-->
    <meta-data 
        android:name="xposedmodule" 
        android:value="true" />
    <!--模块描述-->
    <meta-data 
        android:name="xposeddescription"
        android:value="by:xmnh" />
    <!--模块最小支持xposed版本-->
    <meta-data 
        android:name="xposedminversion"
        android:value="53" />
    <!--模块作用范围-->
    <meta-data 
        android:name="xposedscope"
        android:resource="@array/xposed_scope" />
</application>
```

### 新建scope.xml文件
---
> 在res的values新建scope.xml文件，(鼠标右击后点new，指向Values Resource File)
![img.png](../../images/soul-dream/img_4.png)
![img.png](../../images/soul-dream/img_5.png)
![img.png](../../images/soul-dream/img_8.png)
---
### 修改settings.gradle（最外层项目级别的settings.gradle）
  ![img.png](../../images/soul-dream/img_9.png)
  ![img.png](../../images/soul-dream/img_10.png)

```groovy
pluginManagement {
    repositories {
        maven { url 'https://maven.aliyun.com/repository/gradle-plugin' }
        maven { url 'https://maven.aliyun.com/repository/google' }
        maven { url 'https://maven.aliyun.com/repository/public' }
        maven { url 'https://jitpack.io' }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url 'https://maven.aliyun.com/repository/public' }
        maven { url 'https://maven.aliyun.com/repository/google' }
        maven { url 'https://jitpack.io' }
    }
}
```
---
### 修改settings.gradle（在app-src模块级别的settings.gradle）
**修改gradle都需要重新同步Sync Now**
> 在dependencies加入xposed依赖（可以将其他的依赖先注释）
![img.png](../../images/soul-dream/img_11.png)

```groovy
    compileOnly 'de.robv.android.xposed:api:82'
    compileOnly 'de.robv.android.xposed:api:82:sources'
```
---
### 重新配置gradle版本（点击右上角的齿轮setting，选择Project Structure）
  ![img.png](../../images/soul-dream/img_12.png)

> 再选择Project将Android Gradle Plugin 设置为*7.1.2*版本，Gradle Version设置为*7.5.1*
![img.png](../../images/soul-dream/img_13.png)
> 删除test模块里的测试类，删除后在settings.gradle点击Sync Now
![img.png](../../images/soul-dream/img_14.png)
---
### 点击导航栏的Build再选择Rebuild Project
  ![img.png](../../images/soul-dream/img_15.png)

---
### 在app->res->main里新建一个资源文件（右击鼠标->New->Directory）
  ![img.png](../../images/soul-dream/img_16.png)

> 点击选择assets后按Enter
![img.png](../../images/soul-dream/img_17.png)

> 接着在assets新建文件xposed_init（New File）*文件名是固定的不能更改!改文件为xposed配置文件*
![img.png](../../images/soul-dream/img_18.png)
---
### 在app->src->main->java里新建包结构（app、enums、factory、interfaces）

```
SoulDream
├── xmnh-souldream-app -- hook的app类
├── xmnh-souldream-utils -- 工具类
├── xmnh-souldream-enums -- 枚举类
├── xmnh-souldream-interfaces -- 接口类
├── xmnh-souldream-factory -- 工厂类
```
![img.png](../../images/soul-dream/img_18.png)

---
### 创建入口类RunHook需要implements IXposedHookLoadPackage
> 并将包名加类名填写到xposed_init
![img.png](../../images/soul-dream/img_20.png)
```java
public class RunHook implements IXposedHookLoadPackage {
    // 上下文
    private Context context;
    // 类加载器
    private ClassLoader classLoader;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        try {
            // 实例化工厂
            BaseHook baseHook = AppFactory.init(loadPackageParam.processName);
            if (baseHook != null) {
                // 调用hook方法
                hookAttach(baseHook::hook);
            }
        } catch (Exception e) {
            XposedBridge.log(e.getMessage());
        }

    }

    /**
     * 加固以及多dex处理方式
     *
     * @param consumer Context上下文,ClassLoader类加载器
     */
    private void hookAttach(BiConsumer<Context, ClassLoader> consumer) {
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            public void beforeHookedMethod(MethodHookParam param) throws Throwable {
                // 获取context上下文
                context = (Context) param.args[0];
                // 获取classLoader类加载器
                classLoader = context.getClassLoader();
                try {
                    if (null != classLoader) {
                        consumer.accept(context, classLoader);
                    }
                } catch (Exception e) {
                    XposedBridge.log("find error =>" + e.getMessage());
                }
            }
        });
    }
}
```
---
### 在factory包下创建工厂类AppFactory

```java
public class AppFactory {
    // 通过工厂类的实例方法返回实例对象
    public static BaseHook init(String packageName) {
        // 根据包名判断是否相等，返回相对应的对象
        if (AppEnum.KU_WO.getPackageName().equals(packageName)) {
          return new KuWo();
        }
        // TODO 待填写需要创建的实例
        return null;
    }
}
```
---
### 在enums包下创建一个枚举类AppEnum，用来定义需要hook的app

```java
public enum AppEnum {
    KU_WO("酷我音乐", "cn.kuwo.player"),
    QQ_LIVE("腾讯视频", "com.tencent.qqlive"),
  // TODO 待填写需要hook的枚举
    ;

    private final String appName;
    private final String packageName;

    AppEnum(String appName, String packageName) {
        this.appName = appName;
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

}
```
---
### 在interfaces包下创建接口BaseHook,该接口用来定义hook的接口，不做实现
```java
@FunctionalInterface
public interface BaseHook {
  /**
   * 作用于hook逻辑处理
   * @param context 上下文（主要用来获取app相关内容，如toast、appVersionCode）
   * @param classLoader 类加载器（由于hook的类不是在本地，需要在hookAttach后才进行加载类）
   */
    void hook(Context context, ClassLoader classLoader);
}
```
---
### 在utils包下创建工具类AppUtil、HookUtil
```java
public class AppUtil {
    private static Toast toast;

    /**
     * 获取app版本名称
     *
     * @param context 上下文
     * @return 返回版本名称
     */
    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取app版本号
     *
     * @param context 上下文
     * @return 返回版本号
     */
    public static int getAppVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 吐司弹窗
     *
     * @param context 上下文
     * @param str     内容
     */
    public static void finish(Context context, String str) {
        String text = str + ": ~ start running ~ ";
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.show();
    }
}
```
```java
public class HookUtil {

    /**
     * 通过反射类找到方法返回类型为void和boolean的所有方法
     * @param cls 类
     * @param val true/false
     */
    public static void booleAndVoidReplace(Class<?> cls, boolean val) {
        for (Method method : cls.getDeclaredMethods()) {
            if (method.getReturnType() == void.class) {
                XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(null));
            }
            if (method.getReturnType() == boolean.class) {
                XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(val));
            }
        }
    }

    /**
     * 通过反射类找到方法返回类型为boolean的所有方法
     * @param cls 类
     * @param val true/false
     */
    public static void booleReplace(Class<?> cls, boolean val) {
        for (Method method : cls.getDeclaredMethods()) {
            if (method.getReturnType() == boolean.class) {
                XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(val));
            }
        }
    }

}
```
---
### 在app包下创建需要hook的app类,如酷我音乐KuWo,需要实现interfaces包下的BaseHook接口
```java
public class KuWo implements BaseHook {
    @Override
    public void hook(Context context, ClassLoader classLoader) {
        int appVersionCode = AppUtil.getAppVersionCode(context);
        if (appVersionCode >= 10050) {
            book(classLoader);
        }
        theme(classLoader);
        ad(classLoader);
        AppUtil.finish(context, AppEnum.KU_WO.getAppName());
    }

    private void book(ClassLoader classLoader) {
        // 看小说
        Class<?> bookAdMgr = XposedHelpers.findClassIfExists("cn.kuwo.ui.book.ad.BookAdMgr", classLoader);
        if (bookAdMgr != null) {
            HookUtil.booleAndVoidReplace(bookAdMgr, false);
        }
    }

    private void theme(ClassLoader classLoader) {
        // 主题
        Class<?> starThemeDetailPresenter = XposedHelpers.findClassIfExists("cn.kuwo.mod.theme.detail.star.StarThemeDetailPresenter", classLoader);
        if (starThemeDetailPresenter != null) {
            XposedHelpers.findAndHookMethod(starThemeDetailPresenter, "checkStarThemeFree",
                    XposedHelpers.findClass("cn.kuwo.mod.theme.bean.star.StarTheme", classLoader),
                    XC_MethodReplacement.returnConstant(true));
        }
    }

    private void ad(ClassLoader classLoader) {
        // 开屏ad
        Class<?> entryActivity = XposedHelpers.findClassIfExists("cn.kuwo.player.activities.EntryActivity", classLoader);
        if (entryActivity != null) {
            for (Method method : entryActivity.getDeclaredMethods()) {
                if (method.getModifiers() == Modifier.PRIVATE
                        && method.getReturnType() == boolean.class
                        && method.getParameterTypes().length == 0) {
                    XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(true));
                }
            }
        }
    }
}
```
---
