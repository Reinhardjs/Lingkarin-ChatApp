package com.lingkarin.dev.chatapp.mvp.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lingkarin.dev.chatapp.R;
import com.lingkarin.dev.chatapp.data.Dependency1;
import com.lingkarin.dev.chatapp.di.component.ActivityComponent;
import com.lingkarin.dev.chatapp.di.component.DaggerActivityComponent;
import com.lingkarin.dev.chatapp.di.module.ActivityModule;
import com.lingkarin.dev.chatapp.mvp.chatlist.ChatListActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private static ActivityComponent activityComponent;

    @Inject
    public Dependency1 dependency1;

    @BindView(R.id.textView)
    TextView textView;

    @BindView(R.id.chatListButton)
    Button chatListButton;

    MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mainPresenter = new MainPresenter(this);

        if (getActivityComponent() == null){
            activityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .build();
        }

        activityComponent.inject(this);
        textView.setText(dependency1.getTitle());


        chatListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
                startActivity(intent);
            }
        });

    }

    public static ActivityComponent getActivityComponent(){
        return activityComponent;
    }
}
