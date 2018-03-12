package be.kunstmaan.shakermenu.demo;

import android.app.Application;
import android.widget.Toast;

import be.kunstmaan.shakermenu.KunstmaanShakerMenu;

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        new KunstmaanShakerMenu.Builder(this)
                .setTitle("My menu")
                .setSensitivity(KunstmaanShakerMenu.Sensitivity.LIGHT)
                .addItem("Title A", new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "item A", Toast.LENGTH_SHORT).show();
                    }
                })
                .addItem("Title B", new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "item B", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

    }
}
