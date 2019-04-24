package com.lingkarin.dev.chatapp.di.module;

import android.app.Activity;
import android.content.Context;

import com.lingkarin.dev.chatapp.data.Dependency1;
import com.lingkarin.dev.chatapp.di.ActivityContext;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private Activity mActivity;

    public ActivityModule(Activity activity){
        mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContext(){
        return mActivity;
    }

    @Provides
    Activity provideActivity(){
        return mActivity;
    }

    @Provides
    Dependency1 getDependency(){
        return new Dependency1("this is dependency 1");
    }
}
