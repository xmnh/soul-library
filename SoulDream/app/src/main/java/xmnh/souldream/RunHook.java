package xmnh.souldream;

import android.app.Application;
import android.content.Context;

import java.util.function.BiConsumer;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import xmnh.souldream.factory.AppFactory;
import xmnh.souldream.interfaces.BaseHook;

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
