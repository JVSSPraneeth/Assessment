package com.macys.assessment.scanner;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;

import com.macys.assessment.R;
import com.macys.assessment.activity.ExternalStorageScanActivity;
import com.macys.assessment.models.ExternalStorageScanResult;

import java.io.File;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by pjatapro on 9/21/16.
 * <p>
 * Background Storage Scan Thread.
 *
 * @extends Thread
 */
@SuppressWarnings("ALL")
public final class ExternalStorageScanThread extends Thread {

    // TAG.
    private final String TAG = getClass().getSimpleName();
    // Instance state members.
    private Context mContext;
    private Handler mMainHandler;
    private Handler mBackgroundHandler;
    private NotificationManager mNotificationManager;
    private ExternalStorageScanResult mScanResult;
    private ExternalStorageScanThread() {
        // Restrict Default Initializers.
    }

    /**
     * Constructor to initialize Background Storage Scanner.
     *
     * @param context
     * @param clientHandler
     */
    public ExternalStorageScanThread(Context context,
                                     Handler clientHandler) {
        mContext = context;
        mMainHandler = clientHandler;
    }

    /**
     * Background Storage Scan executing.
     */
    @Override
    public void run() {
        super.run();

        // Verify Client Handler reference state.
        if (mMainHandler != null) {
            try {

                // Prepare Looper.
                Looper.prepare();

                // Notify Background Storage Scan Handler reference to Main Handler.
                Message msg = mMainHandler.obtainMessage(ExternalStorageScanActivity.
                        ExternalStorageScanMainHandler.MSG_BACKGROUND_HANDLER);
                mMainHandler.removeMessages(ExternalStorageScanActivity.
                        ExternalStorageScanMainHandler.MSG_BACKGROUND_HANDLER);
                mBackgroundHandler = new ExternalStorageScanBackgroundHandler();
                msg.obj = mBackgroundHandler;
                mMainHandler.sendMessage(msg);

                // Activate Looper.
                Looper.loop();
            } catch (Exception ex) {

                // Exception.
                ex.printStackTrace();

                sendMediaNotMountedMsg();
            }
        } else {
            sendMediaNotMountedMsg();
        }
    }

    // Begin Background Storage Scan.
    private void startScan() {

        mNotificationManager = (NotificationManager) mContext.
                getSystemService(NOTIFICATION_SERVICE);

        new Thread() {

            @Override
            public void run() {
                super.run();

                try {
                    showStatusBarNotification();

                    mScanResult = new ExternalStorageScanResult();

                    // Apparently, Stackoverflow.com suggests this is not ideal implementation
                    // to fetch the External Storage Directory. However, haven't had a chance to
                    // test and confirm fully on devices and emulators available.
                    // So far, it does seem to work.
                    scanFile(Environment.getExternalStorageDirectory());
                    mScanResult.setEndTime();

                    if (mBackgroundHandler != null) {
                        Message msg = mBackgroundHandler.obtainMessage(
                                ExternalStorageScanBackgroundHandler.MSG_SCAN_COMPLETED);
                        mBackgroundHandler.removeMessages(
                                ExternalStorageScanBackgroundHandler.MSG_SCAN_COMPLETED);
                        mBackgroundHandler.sendMessage(msg);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();

                    if (mBackgroundHandler != null) {
                        Message msg = mBackgroundHandler.obtainMessage(
                                ExternalStorageScanBackgroundHandler.MSG_SCAN_FAILED);
                        mBackgroundHandler.removeMessages(
                                ExternalStorageScanBackgroundHandler.MSG_SCAN_FAILED);
                        mBackgroundHandler.sendMessage(msg);
                    }
                }
            }

            // Background Storage Scan executing.
            private void scanFile(File file) throws IllegalStateException {
                if ((file != null) && file.exists()) {
                    if (file.isDirectory()) {
                        File[] files = file.listFiles();
                        for (File subFile : files) {
                            scanFile(subFile);
                        }
                    } else {
                        String fileName = file.getAbsolutePath().substring(
                                file.getAbsolutePath().lastIndexOf(File.separator) + 1);
                        mScanResult.addScanResultEntry(fileName, file.length());
                    }
                } else {
                    throw new IllegalStateException("File " + file.getName() + " cannot be read");
                }
            }
        }.start();
    }

    // End Background Storage Scan and clean.
    private void stopScan() {
        mNotificationManager.cancelAll();
        mNotificationManager = null;

        mMainHandler = null;
        mBackgroundHandler = null;
        mScanResult = null;
        mContext = null;
        Looper.myLooper().quit();
    }

    // Notify Background Storage Scan Failure with custom User-Friendly message.
    private void sendMediaNotMountedMsg() {
        Message message = mMainHandler.obtainMessage(ExternalStorageScanActivity.
                ExternalStorageScanMainHandler.MSG_RELEASE_BACKGROUND_HANDLER);
        mMainHandler.removeMessages(ExternalStorageScanActivity.
                ExternalStorageScanMainHandler.MSG_RELEASE_BACKGROUND_HANDLER);
        message.arg1 = ExternalStorageScanActivity.ExternalStorageScanMainHandler.MSG_ARG_ERROR;
        message.obj = mContext.getText(R.string.err_msg_external_storage_read_fail);
        mMainHandler.sendMessage(message);

        stopScan();
    }

    // Notify Background Storage Scan Completion.
    private void signalScanComplete() {
        Message msg = mMainHandler.obtainMessage(ExternalStorageScanActivity.
                ExternalStorageScanMainHandler.MSG_RELEASE_BACKGROUND_HANDLER);
        mMainHandler.removeMessages(ExternalStorageScanActivity.
                ExternalStorageScanMainHandler.MSG_RELEASE_BACKGROUND_HANDLER);
        msg.arg1 = ExternalStorageScanActivity.ExternalStorageScanMainHandler.MSG_ARG_SCAN_COMPLETE;
        msg.obj = mScanResult;
        mMainHandler.sendMessage(msg);

        stopScan();
    }

    // Status Bar Notification.
    private void showStatusBarNotification() {

        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = mContext.getText(R.string.external_storage_scan_started);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                new Intent(mContext, mContext.getClass()), 0);

        // Set the info for the views that show in the notification panel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext);
        notificationBuilder.setSmallIcon(R.drawable.ic_sd_card_white_24dp)  // the status icon
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.ic_sd_card_black_36dp)) // the large icon in status-window
                .setColor(ContextCompat.getColor(mContext, android.R.color.white))  // the background color
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(mContext.getText(R.string.external_storage_scan_started))  // the label of the entry
                .setContentIntent(contentIntent); // The intent to send when the entry is clicked

        // Send the status-bar notification.
        mNotificationManager.notify(R.string.external_storage_scan_started, notificationBuilder.build());
    }

    /**
     * Background Handler to receive Begin and Interrupt notifications for
     * Storage Scan.
     *
     * @extends Handler
     */
    public final class ExternalStorageScanBackgroundHandler extends Handler {

        // Message to receive Start Scan notification.
        public static final int MSG_START_SCAN = 0;

        // Message to receive Interrupt Scan notification.
        public static final int MSG_STOP_SCAN = 1;

        // Message to notify this handler that Scan failed.
        private static final int MSG_SCAN_FAILED = 2;

        // Message to notify this handler that Scan Completed.
        private static final int MSG_SCAN_COMPLETED = 3;

        private ExternalStorageScanBackgroundHandler() {
            // Class should be referenced but instance should not be created
            // outside the context of this Thread.
        }

        /**
         * Handle Messages from Main UI Handler.
         *
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                // Message to Begin Storage Scan.
                case MSG_START_SCAN:
                    startScan();
                    break;

                // Message to Interrupt Storage Scan.
                case MSG_STOP_SCAN:

                    Message message = mMainHandler.obtainMessage(ExternalStorageScanActivity.
                            ExternalStorageScanMainHandler.MSG_RELEASE_BACKGROUND_HANDLER);
                    mMainHandler.removeMessages(ExternalStorageScanActivity.
                            ExternalStorageScanMainHandler.MSG_RELEASE_BACKGROUND_HANDLER);
                    message.obj = mContext.getText(R.string.msg_external_storage_scan_interrupted);
                    mMainHandler.sendMessage(message);

                    stopScan();
                    break;

                // Message Storage Scan Failed.
                case MSG_SCAN_FAILED:
                    sendMediaNotMountedMsg();
                    break;

                // Message Storage Scan Completed.
                case MSG_SCAN_COMPLETED:
                    signalScanComplete();
                    break;

                default:
                    break;
            }
        }
    }
}
