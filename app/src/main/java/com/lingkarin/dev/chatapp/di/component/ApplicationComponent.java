package com.lingkarin.dev.chatapp.di.component;

import com.lingkarin.dev.chatapp.di.module.ApplicationModule;
import com.lingkarin.dev.chatapp.di.module.AuthModule;
import com.lingkarin.dev.chatapp.mvp.ChatApplication;
import com.lingkarin.dev.chatapp.mvp.chatlist.ChatListActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {


    AuthComponent newAuthComponent(AuthModule authModule);

    void inject(ChatApplication chatApplication);
    void inject(ChatListActivity chatListActivity);

}
