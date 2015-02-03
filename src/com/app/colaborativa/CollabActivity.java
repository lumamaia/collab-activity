package com.app.colaborativa;

import java.util.Date;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class CollabActivity extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    Parse.initialize(this, "LXc7FTizvcbJyXbUU0wzKMik80Z2nbP9k6O9Bswe", "heaSZecFOoOOIBZHwrY14ieAiTqwfLAN1lGal29O");
    
    ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
      
    // If you would like all objects to be private by default, remove this line.
    defaultACL.setPublicReadAccess(true);
    defaultACL.setPublicWriteAccess(true);
    
    ParseACL.setDefaultACL(defaultACL, true);
  }
}
