package com.lingkarin.dev.chatapp.mvp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lingkarin.dev.chatapp.R;
import com.lingkarin.dev.chatapp.data.AppSettings;
import com.lingkarin.dev.chatapp.mvp.main.MainActivity;
import com.lingkarin.dev.chatapp.services.service.MyService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.usernameET) EditText usernameET;
    @BindView(R.id.passwordET) EditText passwordET;
    @BindView(R.id.signinButton) Button signInButton;
    private String mUsername, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        if (AppSettings.getUserName(getApplicationContext()) != null){
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }

        mUsername = AppSettings.getUserName(getApplicationContext());
        mPassword = AppSettings.getPassword(getApplicationContext());

        signInButton.setOnClickListener(view -> {
            mUsername = usernameET.getText().toString();
            mPassword = passwordET.getText().toString();

            Intent connectIntent = new Intent();
            connectIntent.setAction(MyService.START_CONNECT);
            connectIntent.putExtra("username", mUsername);
            connectIntent.putExtra("password", mPassword);

            EventBus.getDefault().post(connectIntent);
        });

        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccessful(Intent intent) {
        switch (intent.getAction()){
            case MyService.CONNECT_SUCCESSFUL:
                AppSettings.setUserName(getApplicationContext(), mUsername);
                AppSettings.setPassword(getApplicationContext(), mPassword);

                EventBus.getDefault().unregister(this);

                Toast.makeText(getApplicationContext(), "Login Berhasil", Toast.LENGTH_LONG).show();
                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
                break;
            case MyService.CONNECT_FAIL:
                Toast.makeText(getApplicationContext(), "Login Gagal", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void onDestroy(){
        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }
}
