package com.lingkarin.dev.chatapp.mvp.main;

import android.content.Context;

public class MainPresenter implements MainContract.Presenter {
    MainActivity mView;

    public MainPresenter(MainActivity activity){
        mView = activity;
        Context context = (Context) mView;

    }

}
