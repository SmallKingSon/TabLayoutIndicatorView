package app.com.tvrecyclerview;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * android开发工具类
 */
public class Yr {

    /**
     * 根据给定的id找到控件
     *
     * @param t
     * @param id
     * @param _this
     * @return
     */
    private static Context mContext;

    private static String logFilterRegex = ".*";
    private static String charset = "UTF-8";
    //
    private static Handler handler;
    private static int logLength = 7;
    private static boolean isDebug = true;
    public final static int LOG_LEVEL_V = 0;
    public final static int LOG_LEVEL_D = 1;
    public final static int LOG_LEVEL_I = 2;
    public final static int LOG_LEVEL_W = 3;
    public final static int LOG_LEVEL_E = 4;
    public final static String TAG = "Y->";
    public static int sLogLevel = LOG_LEVEL_D;

    public static void setCharset(String charset) {
        Yr.charset = charset;
    }

    public static void setLogFilterRegex(String logFilterRegex) {
        Yr.logFilterRegex = logFilterRegex;
    }

    public static boolean isNull(EditText et) {

        if (et.getText().toString() == null) {
            return true;
        }
        if (et.getText().toString().trim().equals("")) {
            return true;
        }
        return false;
    }
    public static void v() {
        Yr.log(LOG_LEVEL_V, new Throwable().getStackTrace()[1]);
    }
    public static void v(Object... values) {
        Yr.log(LOG_LEVEL_V, new Throwable().getStackTrace()[1],values);
    }
    public static void d() {
        Yr.log(LOG_LEVEL_D, new Throwable().getStackTrace()[1]);
    }
    public static void d(Object... values) {
        Yr.log(LOG_LEVEL_D, new Throwable().getStackTrace()[1],values);
    }
    public static void i() {
        Yr.log(LOG_LEVEL_I, new Throwable().getStackTrace()[1]);
    }
    public static void i(Object... values) {
        Yr.log(LOG_LEVEL_I, new Throwable().getStackTrace()[1],values);
    }
    public static void w() {
        Yr.log(LOG_LEVEL_W, new Throwable().getStackTrace()[1]);
    }
    public static void w(Object... values) {
        Yr.log(LOG_LEVEL_W, new Throwable().getStackTrace()[1],values);
    }
    public static void e() {
        Yr.log(LOG_LEVEL_E, new Throwable().getStackTrace()[1]);
    }
    public static void e(Object... values) {
        Yr.log(LOG_LEVEL_E, new Throwable().getStackTrace()[1],values);
    }
    public static void log(int level, StackTraceElement trace, Object... values) {
        if (!isDebug()) {
            return ;
        }
        String str = null;
        if (values != null) {
            str = getLogValues(getLogLength(), values);
        }
        StringBuilder builder = new StringBuilder();
        builder.append("[")
                .append(android.os.Process.myPid())
                .append("$")
                .append(Thread.currentThread().getName())
                .append("]")
                .append("(")
                .append(trace.getFileName())
                .append(":")
                .append(trace.getLineNumber())
                .append(")")
//                .append(getSimpleClassName(trace.getClassName()))
//                .append(".")
                .append("<")
                .append(trace.getMethodName())
                .append(">");
        if (str!=null) {
            builder.append(":")
                    .append(str);
        }
        switch (level) {
            case LOG_LEVEL_V:
                Log.v(TAG, builder.toString());
                break;
            case LOG_LEVEL_D:
                Log.d(TAG, builder.toString());
                break;
            case LOG_LEVEL_I:
                Log.i(TAG, builder.toString());
                break;
            case LOG_LEVEL_W:
                Log.w(TAG, builder.toString());
                break;
            case LOG_LEVEL_E:
                Log.e(TAG, builder.toString());
                break;
        }
        return;
    }
    private static String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    private static boolean isPass(String tag) {
        if (tag.matches(logFilterRegex)) {
            return true;
        }
        return false;
    }
    public static String createLogTag(int stackCount) {
        StackTraceElement stack[] = new Throwable().getStackTrace();
        StackTraceElement stackMsg = stack[stackCount];
        String className = stackMsg.getClassName();
        className = className.substring(className.lastIndexOf(".") + 1);
        String name = Thread.currentThread().getName();
        if (name.length() > 15) {
            name = name.substring(0, 15);
        }
        String threadTag = "[" + name + "] ";
        return "Y->" + threadTag + className + "." + stackMsg.getMethodName()
                + " line: " + stackMsg.getLineNumber();
    }
    private static String getLogValues(int length, Object... values) {
        StringBuilder mainBuilder = new StringBuilder();

        for (Object object : values) {
            if (object == null) {
                object = "null";
            }
            String s = object.toString();
            if (s.length()>1024) {
                s = s.substring(0, 1024);
            }
            StringBuilder sb = new StringBuilder(s);

            while (sb.length() < length) {
                sb.append(" ");
            }

            sb.append("| ");

            mainBuilder.append(sb);
        }
        return mainBuilder.toString();
    }

    /**
     * 抛出错误的具体行号
     *
     * @param e catch捕获到的Exception
     */
    public static void logError(Exception e) {
        StringBuffer sb = new StringBuffer();
        StackTraceElement[] stackTrace = e.getStackTrace();
        sb.append(">>>>>>>>>>      " + e.toString() + " at :     <<<<<<<<<<"
                + "\n");
        for (int i = 0; i < stackTrace.length; i++) {
            if (i < 10) {
                StackTraceElement stackTraceElement = stackTrace[i];
                String errorMsg = stackTraceElement.toString();
                sb.append(errorMsg).append("\n");
            } else {
                sb.append("more : " + (stackTrace.length - 10) + "..." + "\n");
                break;
            }
        }
        sb.append(">>>>>>>>>>     end of error     <<<<<<<<<<");
        log(LOG_LEVEL_E,new Throwable().getStackTrace()[1],sb.toString());
    }

    /**
     * 抛出错误的具体行号
     *
     * @param e catch捕获到的Exception
     */
    public static void logError(String msg, Exception e) {
        StringBuffer sb = new StringBuffer();
        StackTraceElement[] stackTrace = e.getStackTrace();
        sb.append(">>>>>>>>>>      " + e.toString() + " at :     <<<<<<<<<<"
                + "\n");
        for (int i = 0; i < stackTrace.length; i++) {
            if (i < 15) {
                StackTraceElement stackTraceElement = stackTrace[i];
                String errorMsg = stackTraceElement.toString();
                sb.append(errorMsg).append("\n");
            } else {
                sb.append("more : " + (stackTrace.length - 15) + "..." + "\n");
                break;
            }
        }
        sb.append(">>>>>>>>>>     end of error     <<<<<<<<<<");
//        e(msg == null ? sb.toString() : msg + "---" + sb.toString(), 4);
        log(Yr.LOG_LEVEL_E,new Throwable().getStackTrace()[1],msg == null ? sb.toString() : msg + "---" + sb.toString(), 4);
    }

    public static void toast(String msg) {
        if (mContext == null) {
            try {
                throw new Exception("得先初始化context！");
            } catch (Exception e) {
                e(e, 2);
            }
        } else {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }

    }

    public static Toast result;

    /**
     * @param msg
     * @param layoutRes 这个参数中必须是个GroupView
     */
    public static void toast(String msg, int layoutRes) {

        if (result != null) {
            result.cancel();
        }
        result = new Toast(mContext);
        LayoutInflater inflate = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(layoutRes, null);

        // TextView tv = (TextView) v.findViewById(R.id.textView1);
        TextView tv = getTextView((ViewGroup) v);

        tv.setText(msg);

        result.setView(v);
        result.setGravity(Gravity.BOTTOM, 0, 50);
        result.setDuration(Toast.LENGTH_SHORT);
        result.show();
    }

    public static void toast(String msg, int layoutRes, int x, int y) {

        if (result != null) {
            result.cancel();
        }
        result = new Toast(mContext);
        LayoutInflater inflate = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(layoutRes, null);

        // TextView tv = (TextView) v.findViewById(R.id.textView1);
        TextView tv = getTextView((ViewGroup) v);

        tv.setText(msg);

        result.setView(v);
        result.setGravity(Gravity.BOTTOM, x, y);
        result.setDuration(Toast.LENGTH_SHORT);
        result.show();
    }

    private static TextView getTextView(ViewGroup vg) {

        for (int i = 0; i < vg.getChildCount(); i++) {

            View v = vg.getChildAt(i);
            if (v instanceof TextView) {

                return (TextView) v;

            } else if (v instanceof ViewGroup) {

                return getTextView((ViewGroup) v);
            }

        }
        return null;
    }

    public static void init(Context context) {

        initContext(context.getApplicationContext());

        initHandle();

    }

    public static Context getContext() {

        if (mContext == null) {
            throw new RuntimeException(
                    "Context为空，请在Application.onCreate执行Yr.init(this)");
        }
        return mContext;
    }

    /**
     * 初始化全局Context
     *
     * @param context
     */
    private static void initContext(Context context) {

        if (context == null) {
            return;
        }
        if (mContext != null) {
            return;
        }
        mContext = context;
    }

    public static void runOnUiThread(Runnable r) {
        handler.post(r);
    }

    public static String getCharset() {
        return charset;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static void setDebug(boolean isDebug) {
        Yr.isDebug = isDebug;
    }

    private static void initHandle() {
        handler = new Handler();
    }

    public static int getLogLength() {
        return logLength;
    }

    public static void setLogLength(int logLength) {
        Yr.logLength = logLength;
    }
}
