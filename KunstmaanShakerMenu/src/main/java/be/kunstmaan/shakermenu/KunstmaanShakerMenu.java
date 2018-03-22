package be.kunstmaan.shakermenu;

import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;


import com.squareup.seismic.ShakeDetector;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.SENSOR_SERVICE;

public class KunstmaanShakerMenu {

    public static class Builder {

        private String mTitle = "";
        private final List<String> mNames;
        private final Map<String, Runnable> mRunnables;
        private final Application mApplication;
        private int mAccelerationThreshold;

        public Builder addItem(String name, Runnable runnable) {
            mRunnables.put(name, runnable);
            mNames.add(name);
            return this;
        }

        public Builder setTitle(String titleText) {
            mTitle = titleText;
            return this;
        }

        public Builder setSensitivity(Sensitivity sensitivityLevel) {
            mAccelerationThreshold = sensitivityLevel.value;
            return this;
        }

        public Builder(Application application) {
            mApplication = application;
            mNames = new ArrayList<>();
            mRunnables = new HashMap<>();
        }

        public void build() {
            KunstmaanShakerMenu.sTitle = this.mTitle;
            KunstmaanShakerMenu.sNames = this.mNames;
            KunstmaanShakerMenu.sRunnables = this.mRunnables;
            KunstmaanShakerMenu.sAccelerationThreshold = mAccelerationThreshold;

            KunstmaanShakerMenu.init(mApplication);
        }

    }

    public enum Sensitivity {
        LIGHT(11), MEDIUM(13), HARD(15);

        private final int value;

        Sensitivity(int value) {
            this.value = value;
        }
    }

    private static List<String> sNames;
    private static Map<String, Runnable> sRunnables;
    private static String sTitle = "";
    private static int sAccelerationThreshold;

    private static WeakReference<Activity> sActivity;
    private static ShakeDetector sShakeDetector;
    private static SensorManager sSensorManager;

    public static void enable(){
        if(sSensorManager != null && sShakeDetector != null){
            sShakeDetector.start(sSensorManager);
        }
    }

    public static void disable(){
        if(sShakeDetector != null){
            sShakeDetector.stop();
        }
    }

    public static void trigger(){
        if(sShakeDetector != null){
            shakeListener.hearShake();
        }
    }

    private static void init(Application application) {
        setupActivityGrabber(application);
        sSensorManager = (SensorManager) application.getApplicationContext().getSystemService(SENSOR_SERVICE);
        sShakeDetector = new ShakeDetector(shakeListener);
        if (sAccelerationThreshold > 0) {
            sShakeDetector.setSensitivity(sAccelerationThreshold);
        }
        sShakeDetector.start(sSensorManager);
    }

    private final static ShakeDetector.Listener shakeListener = new ShakeDetector.Listener() {
        @Override
        public void hearShake() {
            sShakeDetector.stop();
            AlertDialog.Builder builder = new AlertDialog.Builder(sActivity.get());
            builder.setTitle(sTitle);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(sActivity.get(), android.R.layout.simple_list_item_1, sNames);
            builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sRunnables.get(sNames.get(which)).run();
                    sShakeDetector.start(sSensorManager);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    sShakeDetector.start(sSensorManager);
                }
            });
            dialog.show();
        }
    };

    private static void setupActivityGrabber(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                sActivity = new WeakReference<>(activity);
                sShakeDetector.start(sSensorManager);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                sActivity.clear();
                sShakeDetector.stop();
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }
        });
    }
}
