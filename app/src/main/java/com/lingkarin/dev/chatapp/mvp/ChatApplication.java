package com.lingkarin.dev.chatapp.mvp;

import android.app.Application;

import com.lingkarin.dev.chatapp.di.component.ApplicationComponent;
import com.lingkarin.dev.chatapp.di.component.AuthComponent;
import com.lingkarin.dev.chatapp.di.component.DaggerApplicationComponent;
import com.lingkarin.dev.chatapp.di.module.ApplicationModule;
import com.lingkarin.dev.chatapp.di.module.AuthModule;

public class ChatApplication extends Application {

    private static ApplicationComponent applicationComponent;
    private static AuthComponent authComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (getApplicationComponent() == null) {
            // applicationComponent = DaggerApplicationComponent.create();
            applicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this)).build();
        }

        if (getAuthComponent() == null){
            authComponent = applicationComponent.newAuthComponent(
                    new AuthModule("user123", "password123"));
        }
    }

    public static ApplicationComponent getApplicationComponent(){
        return applicationComponent;
    }

    public static AuthComponent getAuthComponent(){
        return authComponent;
    }

}
