package xmnh.souldream.enums;

public enum AppEnum {
    KU_WO("酷我音乐","cn.kuwo.player"),
    QQ_LIVE("腾讯视频","com.tencent.qqlive"),
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
