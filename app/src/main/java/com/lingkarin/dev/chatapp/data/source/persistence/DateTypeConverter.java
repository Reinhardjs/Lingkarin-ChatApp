package com.lingkarin.dev.chatapp.data.source.persistence;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;

public class DateTypeConverter implements Serializable {
    @TypeConverter
    public String fromDate(Date date){
        if (date == null){
            return null;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Date>(){}.getType();

        String json = gson.toJson(date, type);
        return json;
    }

    @TypeConverter
    public Date toDate(String date){

        if (date == null){
            return null;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Date>(){}.getType();

        Date dateData = gson.fromJson(date, type);
        return dateData;
    }
}
