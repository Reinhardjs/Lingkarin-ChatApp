package com.lingkarin.dev.chatapp.data.source.persistence;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lingkarin.dev.chatapp.data.models.User;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;

public class UserTypeConverter implements Serializable {
    @TypeConverter
    public String fromUser(User user){
        if (user == null){
            return null;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<User>(){}.getType();

        return gson.toJson(user, type);
    }

    @TypeConverter
    public User toUser(String user){

        if (user == null){
            return null;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<User>(){}.getType();

        return gson.fromJson(user, type);
    }
}
