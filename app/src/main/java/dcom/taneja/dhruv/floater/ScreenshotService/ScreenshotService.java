package dcom.taneja.dhruv.floater.ScreenshotService;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

public class ScreenshotService extends AppCompatActivity {
    public String pathToFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button updateButton = (Button) findViewById(R.id.screenshotButton);
        updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                updateTimer();
                takeScreenshot();
            }
        });
    }


    private void updateTimer() {
        TextView timer = (TextView) findViewById(R.id.editText);

        timer.setText(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
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
}
