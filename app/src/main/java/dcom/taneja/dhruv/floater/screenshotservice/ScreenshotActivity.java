package dcom.taneja.dhruv.floater.screenshotservice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import dcom.taneja.dhruv.floater.R;

/**
 * Created by PrateekGarg on 1/12/2018.
 */

public class ScreenshotActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 537;
    private String mPathToFile;
    private String TAG = ScreenshotActivity.class.getSimpleName();
    private TextView mTextViewTimer;
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screenshot_activity);
        mLayout = findViewById(R.id.main_activity);
        findViewById(R.id.button_screenshot_action).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                beginScreenshotTaker();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_EXTERNAL_STORAGE) {
            // Request for write permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start screenshot preview Activity.
                Snackbar.make(mLayout, "Write permission was granted. Starting preview.",
                        Snackbar.LENGTH_SHORT)
                        .show();
                takeScreenshot();
            } else {
                // Permission request was denied.
                Snackbar.make(mLayout, "Write permission request was denied.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    private void beginScreenshotTaker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // BEGIN_INCLUDE(startScreenshot)
            // Check if the Write permission has been granted
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permission is already available, start screenshot
                takeScreenshot();
            } else {
                // Permission is missing and must be requested.
                requestExternalStoragePermission();
            }
            // END_INCLUDE(startScreenshot)
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            takeScreenshot();
        }
    }

    private void requestExternalStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(mLayout, "Write access is required to start screenshot.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(ScreenshotActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_EXTERNAL_STORAGE);
                }
            }).show();

        } else {

            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_EXTERNAL_STORAGE);
        }
    }

    private void takeScreenshot() {

        //Activity code starting here
        try {
            updateTimer();
            String now = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            // image naming and path  to include sd card  appending name you choose for file

            mPathToFile = getFilesDir().getAbsolutePath() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPathToFile);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            Log.i("Path to file", mPathToFile);
            //Sample saved file path: /data/user/0/dcom.taneja.dhruv.floater/files/20180124_014515.jpg

            setImage();
        } catch (IOException e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void setImage() {
        ImageView imageView = findViewById(R.id.image_screenshot);
        Bitmap bitmap = BitmapFactory.decodeFile(mPathToFile);

        imageView.setImageBitmap(bitmap);
    }

    private void updateTimer() {
        mTextViewTimer = findViewById(R.id.text_timestamp);
        mTextViewTimer.setText(android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", new Date()));
    }

    public String getPathToFile() {
        return this.mPathToFile;
    }

    public Uri getScreenshotUri() {
        return Uri.fromFile(new File(getPathToFile()));

    }

}
