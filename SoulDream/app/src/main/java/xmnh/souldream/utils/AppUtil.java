package xmnh.souldream.utils;

import android.content.Context;
import android.widget.Toast;

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
