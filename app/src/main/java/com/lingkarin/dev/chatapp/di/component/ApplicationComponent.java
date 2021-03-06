package com.lingkarin.dev.chatapp.di.component;

import android.content.Context;

import com.lingkarin.dev.chatapp.di.module.ApplicationModule;
import com.lingkarin.dev.chatapp.di.module.AuthModule;
import com.lingkarin.dev.chatapp.ChatApplication;
import com.lingkarin.dev.chatapp.mvp.chat.ChatActivity;
import com.lingkarin.dev.chatapp.mvp.chatlist.ChatListActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {


    AuthComponent newAuthComponent(AuthModule authModule);

    Context getContext();

    void inject(ChatApplication chatApplication);
    void inject(ChatActivity chatActivity);
//    void inject(ChatListActivity chatListActivity);

}
