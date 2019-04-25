package com.lingkarin.dev.chatapp.di.component;

import com.lingkarin.dev.chatapp.data.Credential;
import com.lingkarin.dev.chatapp.di.AuthScope;
import com.lingkarin.dev.chatapp.di.module.AuthModule;
import com.lingkarin.dev.chatapp.mvp.chatlist.ChatListActivity;
import com.lingkarin.dev.chatapp.mvp.main.MainActivity;

import dagger.Subcomponent;

@AuthScope
@Subcomponent(modules = AuthModule.class)
public interface AuthComponent {

    Credential getCredential();

    void inject(ChatListActivity chatListActivity);
    void inject(MainActivity mainActivity);

}
