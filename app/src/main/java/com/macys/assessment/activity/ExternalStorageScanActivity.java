package com.macys.assessment.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.macys.assessment.R;
import com.macys.assessment.adapters.FileSizeAdapter;
import com.macys.assessment.adapters.FileTypeCountAdapter;
import com.macys.assessment.models.ExternalStorageScanResult;
import com.macys.assessment.scanner.ExternalStorageScanThread;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by pjatapro on 9/21/16.
 * <p>
 * Storage Scan Window UI Activity for initiating start as well as
 * displaying Scan Results.
 *
 * @extends AppCompatActivity
 * @implements View.OnClickListener
 */
@SuppressWarnings("ALL")
public final class ExternalStorageScanActivity extends AppCompatActivity implements
        View.OnClickListener {

    // TAG.
    private final String TAG = getClass().getSimpleName();
    // Manifest Request Permission Code.
    private final int REQUEST_READ_WRITE_EXTERNAL_STORAGE = 0;
    // Background Handler reference.
    private Handler mBackgroundHandler;
    // UI Elements.
    private TextView mFabText;
    private TextView mStatusText;
    private TextView mAverageFileSize;
    private FloatingActionButton mFab;
    private LinearLayout mScanContent;
    private LinearLayout mProgressContainer;
    private LinearLayout mScanResultsContainer;
    private RecyclerView mBiggestFiles;
    private RecyclerView mFileTypes;

    /**
     * Activity UI Life-cycle callback to Setup View components.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_external_storage_scan);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            try {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setDisplayUseLogoEnabled(true);
                getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
            } catch (Exception ex) {
                Log.e(TAG, "Exception - ", ex);
            }
        }

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFabText = (TextView) findViewById(R.id.fab_text);
        mStatusText = (TextView) findViewById(R.id.status);
        mAverageFileSize = (TextView) findViewById(R.id.averageFileSize);
        mScanContent = (LinearLayout) findViewById(R.id.scanContent);
        mProgressContainer = (LinearLayout) findViewById(R.id.progressContainer);
        mScanResultsContainer = (LinearLayout) findViewById(R.id.scanResults);
        mBiggestFiles = (RecyclerView) findViewById(R.id.biggestFiles);
        mFileTypes = (RecyclerView) findViewById(R.id.fileTypes);

        // Enable this window-context to react to FAB click events.
        mFab.setOnClickListener(this);

        // Setup LinearLayoutManager instances for RecyclerViews.
        mBiggestFiles.setLayoutManager(new GridLayoutManager(this, 1,
                LinearLayoutManager.VERTICAL, false));
        mFileTypes.setLayoutManager(new GridLayoutManager(this, 1,
                LinearLayoutManager.VERTICAL, false));

        // Check and Request for READ_EXTERNAL_STORAGE and
        // WRITE_EXTERNAL_STORAGE Manifest permissions.
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_READ_WRITE_EXTERNAL_STORAGE);
        }
    }

    /**
     * Activity UI Life-cycle Callback to setup Default ActionBar menu options.
     *
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sdcard_scan, menu);
        return true;
    }

    /**
     * Activity UI Life-cycle Callback reacting to Default ActionBar menu option selection.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            if (mScanResultsContainer.getVisibility() == View.VISIBLE) {
                shareScanResults();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Activity UI Callback to react to Device Back click events.
     */
    @Override
    public void onBackPressed() {
        if (mBackgroundHandler != null) {
            mFab.performClick();
        }
        super.onBackPressed();
    }

    /**
     * Activity UI Callback to react to Click Events.
     *
     * @param clickedView
     */
    @Override
    public void onClick(View clickedView) {

        // FAB is clicked.
        if (clickedView.equals(mFab)) {
            if (mBackgroundHandler == null) {
                startScan();
            } else {
                stopScan();
            }
        }
    }

    /**
     * Activity UI Callback for Manifest Permissions.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_READ_WRITE_EXTERNAL_STORAGE) {
            for (int index = 0; index < permissions.length; index++) {
                String permission = permissions[index];
                int grantResult = grantResults[index];

                if (permission.equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mFabText.setText("!!!");
                        mFab.setClickable(false);
                        mScanContent.setVisibility(View.VISIBLE);
                        mStatusText.setText(getText(R.string.external_storage_invalid_permissions));
                        mProgressContainer.setVisibility(View.GONE);
                        mScanResultsContainer.setVisibility(View.GONE);
                        break;
                    }
                }
            }
        }
    }

    // Client Initiated Start Storage Scan.
    private void startScan() {
        new ExternalStorageScanThread(this, new ExternalStorageScanMainHandler()).start();
    }

    // Client Initiated Interrupt Storage Scan.
    private void stopScan() {
        mStatusText.setText(getText(R.string.msg_external_storage_scan_interrupted));
        if (mBackgroundHandler != null) {
            Message msg = mBackgroundHandler.obtainMessage(ExternalStorageScanThread.
                    ExternalStorageScanBackgroundHandler.MSG_STOP_SCAN);
            mBackgroundHandler.removeMessages(ExternalStorageScanThread.
                    ExternalStorageScanBackgroundHandler.MSG_STOP_SCAN);
            mBackgroundHandler.sendMessage(msg);
        }
    }

    // Background Storage Scan has begun.
    private void doPostStartScan() {
        mFabText.setText(getText(R.string.label_stop));
        mScanContent.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.VISIBLE);
        mScanResultsContainer.setVisibility(View.GONE);
    }

    // Background Storage Scan has failed.
    private void doOnError(CharSequence errMsg) {
        mScanContent.setVisibility(View.VISIBLE);
        mFabText.setText(getText(R.string.label_start));
        mStatusText.setVisibility(View.VISIBLE);
        mStatusText.setText(errMsg);
        mProgressContainer.setVisibility(View.GONE);
        mScanResultsContainer.setVisibility(View.GONE);
    }

    // Background Storage Scan is complete.
    private void doPostScanComplete(ExternalStorageScanResult scanResult) {

        Long durationInMilliSeconds = scanResult.getDurationLapsed();
        String duration = String.format(Locale.getDefault(),
                "%d min: %d sec: %04d millis",
                TimeUnit.MILLISECONDS.toMinutes(durationInMilliSeconds),
                TimeUnit.MILLISECONDS.toSeconds(durationInMilliSeconds)
                        % TimeUnit.MINUTES.toSeconds(1),
                TimeUnit.MILLISECONDS.toMillis(durationInMilliSeconds)
                        % TimeUnit.SECONDS.toMillis(1));

        mScanContent.setVisibility(View.VISIBLE);
        mFabText.setText(getText(R.string.label_start));
        String statusText = getString(R.string.msg_external_storage_scan_duration) +
                " " + duration;
        mStatusText.setText(statusText);
        mStatusText.setVisibility(View.VISIBLE);

        mProgressContainer.setVisibility(View.GONE);
        mScanResultsContainer.setVisibility(View.VISIBLE);
        String averageSize = " " + Formatter.formatFileSize(this, scanResult.getAverageFileSize());
        mAverageFileSize.setText(averageSize);

        mBiggestFiles.swapAdapter(new FileSizeAdapter(this,
                scanResult.getFileSizeList(10)), true);
        mFileTypes.swapAdapter(new FileTypeCountAdapter(this,
                scanResult.getFileTypesCount(5)), true);
    }

    // Background Storage Scan is interrupted.
    private void doPostStopScan(CharSequence msg) {
        mScanContent.setVisibility(View.VISIBLE);
        mFabText.setText(getText(R.string.label_start));
        mStatusText.setVisibility(View.VISIBLE);
        mStatusText.setText(msg);
        mProgressContainer.setVisibility(View.GONE);
        mScanResultsContainer.setVisibility(View.GONE);
    }

    // Share Storage Scan Results as Email Attachment.
    private void shareScanResults() {
        try {
            Bitmap scanResultImage = Bitmap.createBitmap(mScanResultsContainer.getWidth(),
                    mScanResultsContainer.getHeight(),
                    Bitmap.Config.ARGB_8888);
            //Draw the view inside the Bitmap
            mScanResultsContainer.draw(new Canvas(scanResultImage));

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            boolean isCompressed = scanResultImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            if (!isCompressed) {
                Toast.makeText(this, R.string.label_scan_result_share_failed, Toast.LENGTH_LONG);
                return;
            }

            File scanResult = new File(getFilesDir(), "scan_result.jpg");

            if (scanResult.exists()) {
                scanResult.delete();
            }
            scanResult.createNewFile();

            FileOutputStream fo = new FileOutputStream(scanResult);
            fo.write(bytes.toByteArray());

            bytes.flush();
            bytes.close();
            fo.close();

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("image/*");
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                    getText(R.string.scan_result_share_email_subject));
            emailIntent.putExtra(Intent.EXTRA_TEXT, getText(R.string.scan_result_Share_email_text));
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(scanResult));

            startActivity(Intent.createChooser(emailIntent,
                    getText(R.string.scan_result_share_email_intent_chooser_title)));
        } catch (Exception e) {
            // Duck the Exception on the Main Thread, explicitly.
        }

    }

    /**
     * Handler to receive communication from background scanner.
     *
     * @extends Handler
     */
    public final class ExternalStorageScanMainHandler extends Handler {

        // Message to receive Background Handler reference.
        public static final int MSG_BACKGROUND_HANDLER = 0;

        // Message to release Background Handler reference.
        public static final int MSG_RELEASE_BACKGROUND_HANDLER = 1;

        // Message indicating Scan Error.
        public static final int MSG_ARG_ERROR = 2;

        // Message indicating Scan Completion.
        public static final int MSG_ARG_SCAN_COMPLETE = 3;

        private ExternalStorageScanMainHandler() {
            // Class should be referenced but instance should not be created
            // outside the context of this Activity.
        }

        /**
         * Handle Messages from the Background Scanner.
         *
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                // Background Handler reference message received.
                case MSG_BACKGROUND_HANDLER:
                    mBackgroundHandler = (Handler) msg.obj;

                    Message message = mBackgroundHandler.obtainMessage(ExternalStorageScanThread.
                            ExternalStorageScanBackgroundHandler.MSG_START_SCAN);
                    mBackgroundHandler.removeMessages(ExternalStorageScanThread.
                            ExternalStorageScanBackgroundHandler.MSG_START_SCAN);
                    mBackgroundHandler.sendMessage(message);
                    doPostStartScan();
                    break;

                // Background Handler reference release message received.
                case MSG_RELEASE_BACKGROUND_HANDLER:
                    switch (msg.arg1) {

                        // Release due to Scan Error.
                        case MSG_ARG_ERROR:
                            doOnError((CharSequence) msg.obj);
                            break;

                        // Release after Scan Completion.
                        case MSG_ARG_SCAN_COMPLETE:
                            doPostScanComplete((ExternalStorageScanResult) msg.obj);
                            break;

                        // Release due to Scan Interruption.
                        default:
                            doPostStopScan((CharSequence) msg.obj);
                            break;
                    }

                    // Release Background Handler reference.
                    mBackgroundHandler = null;
                    break;
            }
        }
    }
}
