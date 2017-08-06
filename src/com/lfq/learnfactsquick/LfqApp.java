package com.lfq.learnfactsquick;

import android.app.Application;

public class LfqApp extends Application{
	private static LfqApp instance;
    public LfqApp() {
        instance = this;
    }

    public static LfqApp getInstance() {
         return instance;
    }
}
