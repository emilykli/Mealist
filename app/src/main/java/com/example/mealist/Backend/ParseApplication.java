package com.example.mealist.Backend;

import android.app.Application;

import com.example.mealist.BuildConfig;
import com.parse.Parse;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // TODO: ParseObject.registerSubclass([model].class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(BuildConfig.PARSE_APP_ID)
                .clientKey(BuildConfig.PARSE_CLIENT_KEY)
                .server(BuildConfig.SERVER_URL)
                .build()
        );
    }
}
