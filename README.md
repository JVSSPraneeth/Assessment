Android Programming Assessment:

This App scans all files on the external storage (SD card) and collects following information:

∙ Names and sizes of 10 biggest files

∙ Average file size

∙ 5 most frequent file extensions (with their frequencies)

Functional Acceptance Criteria met:

∙ Scan Results are displayed in a convenient way.

∙ App allows the user to start and stop scanning.

∙ The app displays progress of ongoing scan.

. Environment.getExternalStorageDirectory().length(), nor (getTotalSpace() - getFreeSpace()) provides a valid deterministic Progress End-limit, hence the ongoing scan progress is indeterminate.

∙ App contains a button allowing the user to share obtained statistics via standard Android

sharing menu (button is not active until the activity receives the statistics from the scan). 

∙ App shows a status bar notification while it scans the external storage.

∙ UI handles screen orientation changes.

∙ When app is sent to background (by pressing HOME button), the scan should continue.

∙ When app is stopped by the user (by pressing BACK button), the scan must be stopped

 immediately. 

Technical Details:

∙ Project is developed in Android Studio.

∙ Project uses all the recent (new) Android SDK features - AppCompat and Design libraries such as CoordinatorLayout, AppBarLayout, CollapsingToolbarLayout, Toolbar, NestedScrollView, RecyclerView, CardView, NotificationCompat, AppCompat, ContextCompat etc.

∙ Project supports all versions starting from Android 4.0.

∙ Accomplished project is posted on Github for review.
