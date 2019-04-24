package com.lingkarin.dev.chatapp.di.component;

import com.lingkarin.dev.chatapp.mvp.main.MainActivity;
import com.lingkarin.dev.chatapp.di.module.ActivityModule;

import dagger.Component;

@Component(modules=ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);

}
