package xmnh.souldream.app;

import android.content.Context;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import xmnh.souldream.enums.AppEnum;
import xmnh.souldream.interfaces.BaseHook;
import xmnh.souldream.utils.AppUtil;
import xmnh.souldream.utils.HookUtil;

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
