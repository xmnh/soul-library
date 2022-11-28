package xmnh.souldream.utils;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;

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
