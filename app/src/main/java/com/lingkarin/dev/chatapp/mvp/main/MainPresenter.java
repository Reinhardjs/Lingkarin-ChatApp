package com.lingkarin.dev.chatapp.mvp.main;

public class MainPresenter implements MainContract.Presenter {
    MainActivity mView;

    public MainPresenter(MainActivity activity){
        mView = activity;
    }

}
