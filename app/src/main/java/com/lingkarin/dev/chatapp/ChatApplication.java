package com.lingkarin.dev.chatapp;

import android.app.Application;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lingkarin.dev.chatapp.data.source.Repository;
import com.lingkarin.dev.chatapp.di.component.ApplicationComponent;
import com.lingkarin.dev.chatapp.di.component.AuthComponent;
import com.lingkarin.dev.chatapp.di.component.DaggerApplicationComponent;
import com.lingkarin.dev.chatapp.di.module.ApplicationModule;
import com.lingkarin.dev.chatapp.di.module.AuthModule;
import com.lingkarin.dev.chatapp.services.service.MyService;
import com.lingkarin.dev.chatapp.utils.AppLifeCycleObserver;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class ChatApplication extends Application {

    private static ApplicationComponent applicationComponent;
    private static AuthComponent authComponent;
    private static Repository mRepo;

    @Override
    public void onCreate() {
        super.onCreate();

        mRepo = Repository.getInstance(getApplicationContext());


        Intent myService = new Intent(this, MyService.class);
        startService(myService);


        if (getApplicationComponent() == null) {
            // applicationComponent = DaggerApplicationComponent.create();
            applicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this)).build();
        }

        if (getAuthComponent() == null){
            authComponent = applicationComponent.newAuthComponent(
                    new AuthModule("user123", "password123"));
        }



        // Observer toJid detect if the app is in background or foreground.
        AppLifeCycleObserver lifeCycleObserver
                = new AppLifeCycleObserver(getApplicationContext());

        // Adding the above observer toJid process lifecycle
        ProcessLifecycleOwner.get()
                .getLifecycle()
                .addObserver(lifeCycleObserver);

        Disposable internetDisposable = ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isConnected -> {
                    if (isConnected){
                        FirebaseFirestore.getInstance().enableNetwork()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // Do online things
                                        // ...
                                    }
                                });

                    } else {
                        FirebaseFirestore.getInstance().disableNetwork()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // Do offline things
                                        // ...
                                    }
                                });

                    }
                });
    }

    public static ApplicationComponent getApplicationComponent(){
        return applicationComponent;
    }

    public static AuthComponent getAuthComponent(){
        return authComponent;
    }

}
