package dcom.taneja.dhruv.floater;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by dhruvtaneja on 10/02/18.
 */

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 123;
    private Intent mServiceIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonStartFloater).setOnClickListener(view -> {
            if (!needsPermission()) {
                startFloater();
            } else {

                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        findViewById(R.id.buttonStopFloater).setOnClickListener(view -> {
            if (mServiceIntent != null) {
                stopService(mServiceIntent);
            }
        });
    }

    private boolean needsPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(getApplicationContext());
    }

    private void startFloater() {
        mServiceIntent = new Intent(this, FloaterService.class);
        startService(mServiceIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            startFloater();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
