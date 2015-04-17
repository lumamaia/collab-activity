package com.app.colaborativa;

import android.app.Application;

import com.app.colaborativa.atividade.Feed;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;
import com.parse.PushService;

public class CollabActivity extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    
    Parse.initialize(this, "LXc7FTizvcbJyXbUU0wzKMik80Z2nbP9k6O9Bswe", "heaSZecFOoOOIBZHwrY14ieAiTqwfLAN1lGal29O");
 // Specify an Activity to handle all pushes by default.
 	PushService.setDefaultPushCallback(this, Feed.class);
    ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
      
    // If you would like all objects to be private by default, remove this line.
    defaultACL.setPublicReadAccess(true);
    defaultACL.setPublicWriteAccess(true);
    
    ParseACL.setDefaultACL(defaultACL, true);
  }
}
