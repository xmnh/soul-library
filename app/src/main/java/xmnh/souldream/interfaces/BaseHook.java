package xmnh.souldream.interfaces;

import android.content.Context;

@FunctionalInterface
public interface BaseHook {
    void hook(Context context, ClassLoader classLoader);
}
