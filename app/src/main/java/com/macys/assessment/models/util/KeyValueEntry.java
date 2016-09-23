package com.macys.assessment.models.util;

import java.util.Comparator;

/**
 * Created by pjatapro on 9/21/16.
 * <p>
 * Utility Data-structure encapsulating Key-Value pair as an Entry.
 */
@SuppressWarnings("ALL")
public final class KeyValueEntry {

    // Key-Value elements.
    private String mKey;
    private Long mValue;
    /**
     * Constructor for Key-Value pair Entry.
     *
     * @param key
     * @param value
     */
    public KeyValueEntry(String key, Long value) {
        mKey = key;
        mValue = value;
    }

    /**
     * Get Key from the Entry.
     *
     * @return key
     */
    public String getKey() {
        return mKey;
    }

    /**
     * Set or replace Key in the Entry.
     *
     * @param key
     */
    public void setKey(String key) {
        this.mKey = key;
    }

    /**
     * Get Value from Entry.
     *
     * @return value
     */
    public Long getValue() {
        return mValue;
    }

    /**
     * Set or replace Value in the Entry.
     *
     * @param value
     */
    public void setValue(Long value) {
        this.mValue = value;
    }

    /**
     * Default Comparator for Value-based Descending-order sorting.
     *
     * @extends Comparator
     */
    public static final class DescendingComparator implements Comparator<KeyValueEntry> {
        @Override
        public int compare(KeyValueEntry element1, KeyValueEntry element2) {
            if (((element1 != null) && (element1.mValue != null)) &&
                    ((element2 != null) && (element2.mValue != null))) {
                return element2.mValue.compareTo(element1.mValue);
            }
            throw new IllegalArgumentException("Invalid KeyValueEntry comparison");
        }
    }
}
