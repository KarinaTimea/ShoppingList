package org.projects.shoppinglist;

import com.firebase.client.Firebase;
import com.flurry.android.FlurryAgent;

/**
 * Created by Karina Timea on 25/04/2016.
 */
public class MyApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FlurryAgent.setLogEnabled(false);
        FlurryAgent.init(this, "XQF26TMB8JB662MDGSVS");
        Firebase.setAndroidContext(this);
    }
}
