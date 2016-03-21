package net.mtjo.baselibrary;

import android.util.Log;

/** 
 * log日志打印，根据是否是调试打印出日志信息
 * @author Moyaofen
 */
public final class Loger {
	public static boolean IS_DEBUG = true;
    public static boolean DEBUG_LOG = true;
    public static boolean SHOW_ACTIVITY_STATE = true;

    public static final void openDebutLog(boolean enable) {
        IS_DEBUG = enable;
        DEBUG_LOG = enable;
    }

    public static final void openActivityState(boolean enable) {
        SHOW_ACTIVITY_STATE = enable;
    }

    /**
     * print debug message
     * @param String msg
     */
    public static final void debug(String msg) {
        if (IS_DEBUG) {
            Log.i("debug", msg);
        }
    }

    /**
     * print debug exception message
     * @param String msg
     * @param Throwable tr
     */
    public static final void debug(String msg, Throwable tr) {
        if (IS_DEBUG) {
            Log.i("debug", msg, tr);
        }
    }
    
    /**
     * print debug state message
     * @param String msg
     * @param Throwable tr
     */
    public static final void state(String packName, String state) {
        if (SHOW_ACTIVITY_STATE) {
            Log.d("activity_state", packName + state);
        }
    }
    
    public static final void debugLog(String packName, String state) {
        if (DEBUG_LOG) {
            Log.d("debug", packName + state);
        }
    }
}
