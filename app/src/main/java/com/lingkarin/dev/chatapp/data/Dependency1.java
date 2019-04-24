package com.lingkarin.dev.chatapp.data;

import javax.inject.Inject;

public class Dependency1 {

    private String mTitle;

    @Inject
    public Dependency1(String title){
        mTitle = title;
    }

    public String getTitle(){
        return mTitle;
    }

}
