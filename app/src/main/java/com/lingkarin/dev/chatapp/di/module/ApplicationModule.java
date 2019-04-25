package com.lingkarin.dev.chatapp.di.module;

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    Application application;

    public ApplicationModule(Application app){
        application = app;
    }

    @Provides
    public Context getContext(){
        return application;
    }

    @Provides
    public Application getApplication(){
        return application;
    }

    @Provides
    public String provideText(){
        return "Hello, World";
    }

}
