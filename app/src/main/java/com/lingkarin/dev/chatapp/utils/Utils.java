package com.lingkarin.dev.chatapp.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lingkarin.dev.chatapp.data.models.Message;
import com.lingkarin.dev.chatapp.data.models.User;
import com.lingkarin.dev.chatapp.mvp.chat.ChatActivity;

import java.lang.reflect.Type;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class Utils {

    public static final String INTERNET_CONNECTED = "INTERNET_CONNECTED";
    public static final String INTERNET_DISCONNECTED = "INTERNET_DISCONNECTED";

    public static String isConnected;

    public static String getInternetStatus(){
        return isConnected;
    }

    public static void setInternetStatus(String status){
        isConnected = status;
    }

    public static String messageToJson(Message message){
        Gson gson = new Gson();
        Type type = new TypeToken<Message>(){}.getType();

        return gson.toJson(message, type);
    }

    public static Message jsonToMessage(String message){
        Gson gson = new Gson();
        Type type = new TypeToken<Message>(){}.getType();

        return gson.fromJson(message, type);
    }

    public static boolean isActivityForeground(Context context, String myPackage) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(Integer.MAX_VALUE);

        // ComponentName componentInfo = runningTaskInfo.get(0).topActivity;

        for (ActivityManager.RunningTaskInfo task : tasks){
            if (myPackage.equals(task.topActivity.getClassName())){
                return true;
            }
        }
        return false;
    }

    public static boolean isInChatRoom(String myUsername, String toUsername) {
        return ChatActivity.myUsername.equals(myUsername) &&
                ChatActivity.chatToUsername.equals(toUsername);
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null && connectivity.getActiveNetworkInfo() != null) {

            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

// Ini bukan yg seperti diinginkan
//    public static boolean isAppRunning(final Context context, final String packageName) {
//        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
//        if (procInfos != null)
//        {
//            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
//                if (processInfo.processName.equals(packageName)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    public static boolean isAppRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }

        return false;
    }

}
