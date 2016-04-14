package io.wyntr.peepster;/*
* By Jorge E. Hernandez (@lalongooo) 2015
* */

import android.app.Application;

import io.wyntr.peepster.file.FileUtils;
import com.parse.Parse;
import com.parse.ParseObject;


public class VideoCompressorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FileUtils.createApplicationFolder();
        ParseObject.registerSubclass(ParseMap.class);
        Parse.initialize(this, "uLnlKp6WTfWClRaCkPiOapBKAyEQfTysrgAJKl5t", "ZmvgpQtQuNDKZ39DrZNMABqc0jHpj09qBNzmoqGs");


    }

}