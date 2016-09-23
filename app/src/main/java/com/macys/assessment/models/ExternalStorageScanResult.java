package com.macys.assessment.models;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.macys.assessment.models.util.KeyValueEntry;
import com.macys.assessment.models.util.SortedList;

import java.util.List;

/**
 * Created by pjatapro on 9/21/16.
 * <p>
 * Storage Scan Result model.
 */
@SuppressWarnings("ALL")
public class ExternalStorageScanResult {

    // Time of creation, Time at notification, to calculate Elapsed Duration.
    private Long mStartTime;
    private Long mEndTime;
    // Largest Files Sorted List in Descending Order of Sizes.
    private List<KeyValueEntry> mBiggestFiles = new SortedList<>(
            new KeyValueEntry.DescendingComparator());
    // File Extension Type Count Sorted List in Descending Order of recurrence.
    private List<KeyValueEntry> mFileTypesCount = new SortedList<>(
            new KeyValueEntry.DescendingComparator());

    /**
     * Default Constructor, marks time of creation.
     */
    public ExternalStorageScanResult() {
        mStartTime = System.currentTimeMillis();
    }

    /**
     * Scan Result Individual File data entry.
     *
     * @param fileName
     * @param fileSize
     */
    public void addScanResultEntry(String fileName, Long fileSize) {
        String fileExtensionType = getFileExtensionType(fileName);

        if (!TextUtils.isEmpty(
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionType))) {

            if (mFileTypesCount.size() == 0) {
                mFileTypesCount.add(new KeyValueEntry(fileExtensionType, 1L));
            } else {
                boolean isIncremented = false;
                for (KeyValueEntry entry : mFileTypesCount) {
                    if (entry.getKey().equalsIgnoreCase(fileExtensionType)) {
                        Long count = entry.getValue();
                        entry.setValue(++count);
                        isIncremented = true;
                        break;
                    }
                }
                if (!isIncremented) {
                    mFileTypesCount.add(new KeyValueEntry(fileExtensionType, 1L));
                }
            }
        }

        mBiggestFiles.add(new KeyValueEntry(fileName, fileSize));
    }

    /**
     * Get Average File Size.
     *
     * @return averageSize
     */
    public Long getAverageFileSize() {
        Long totalSize = 0L;
        for (KeyValueEntry result : mBiggestFiles) {
            totalSize += result.getValue();
        }
        return (totalSize / mBiggestFiles.size());
    }

    /**
     * Get Largest File Sizes list.
     *
     * @param size
     * @return
     */
    public List<KeyValueEntry> getFileSizeList(int size) {
        return mBiggestFiles.subList(0, size);
    }

    /**
     * Get File Extension Type Count list.
     *
     * @param size
     * @return
     */
    public List<KeyValueEntry> getFileTypesCount(int size) {
        return mFileTypesCount.subList(0, size);
    }

    /**
     * Set Storage Scan completion before notifying.
     */
    public void setEndTime() {
        mEndTime = System.currentTimeMillis();
    }

    /**
     * Get Storage Scan Duration Elapsed.
     *
     * @return durationElapsed.
     */
    public Long getDurationLapsed() {
        return mEndTime - mStartTime;
    }

    // Retrieve File Extension Type sub-string from File-name.
    private String getFileExtensionType(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
