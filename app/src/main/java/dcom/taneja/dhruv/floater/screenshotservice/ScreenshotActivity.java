package dcom.taneja.dhruv.floater.screenshotservice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import dcom.taneja.dhruv.floater.R;

/**
 * Created by PrateekGarg on 1/12/2018.
 */

public class ScreenshotActivity extends AppCompatActivity {
    private String pathToFile = null;
    private String TAG = "Permission Information:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screenshot_activity);
        Button updateButton = (Button) findViewById(R.id.screenshotButton);
        updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                updateTimer();
                takeScreenshot();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }

    }
    private void updateTimer() {
        TextView timer = (TextView) findViewById(R.id.editText);
        timer.setText(android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", new Date()));
    }

    private void takeScreenshot() {
        String now = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());


        //Check and ask for permission to write and read storage here:
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
        }


        //Activity code starting here
        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = getFilesDir().getAbsolutePath() + "/" + now + ".jpg";
            pathToFile = mPath;

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            Log.i("Path to file", pathToFile);

            setImage();
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    public String getPathToFile() {
        if (pathToFile != null) {
            return pathToFile;
        }
        Toast.makeText(getApplicationContext(), "No screenshot has been taken yet!",
                Toast.LENGTH_LONG).show();
        //Need to handle this one in a better way
        return null;
    }

    public Uri getScreenshotUri() {
        Uri fileUri = Uri.fromFile(new File(getPathToFile()));
        return fileUri;

    }

    private void setImage() {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);

        imageView.setImageBitmap(bitmap);
    }

}
