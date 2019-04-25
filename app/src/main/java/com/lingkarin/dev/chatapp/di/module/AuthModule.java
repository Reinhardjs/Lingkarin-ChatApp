package com.lingkarin.dev.chatapp.di.module;

import com.lingkarin.dev.chatapp.data.Credential;
import dagger.Module;
import dagger.Provides;

@Module
public class AuthModule {

    Credential credential;

    public AuthModule(){
    }

    public AuthModule(String username, String password){
        credential = new Credential(username, password);
    }

    @Provides
    public Credential provideCredential(){
        return credential;
    }

//    @Provides
//    @Singleton
//    public Credential getCredential(){
//        return new Credential("username", "password");
//    }

}
