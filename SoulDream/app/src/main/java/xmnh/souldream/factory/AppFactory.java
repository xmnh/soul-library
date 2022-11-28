package xmnh.souldream.factory;

import xmnh.souldream.app.KuWo;
import xmnh.souldream.app.QQLive;
import xmnh.souldream.enums.AppEnum;
import xmnh.souldream.interfaces.BaseHook;

public class AppFactory {
    // 通过工厂类的实例方法返回实例对象
    public static BaseHook init(String packageName) {
        // 根据包名判断是否相等，返回相对应的对象
        if (AppEnum.KU_WO.getPackageName().equals(packageName)) {
            return new KuWo();
        }
        if (AppEnum.QQ_LIVE.getPackageName().equals(packageName)) {
            return new QQLive();
        }
        // TODO 待填写需要创建的实例
        return null;
    }

}