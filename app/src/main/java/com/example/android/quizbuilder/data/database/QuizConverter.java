package com.example.android.quizbuilder.data.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class QuizConverter {

    private static Gson gson = new Gson();

    @TypeConverter
    public static List<QuizPage> stringToList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<QuizPage>>() {}.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String listToString(List<QuizPage> pages) {
        return gson.toJson(pages);
    }

    @TypeConverter
    public static Date fromTimestamp(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
