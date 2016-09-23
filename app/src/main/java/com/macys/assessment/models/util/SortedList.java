package com.macys.assessment.models.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by pjatapro on 9/21/16.
 * <p>
 * Sorted List Utility Data-structure, to sort the elements in a given
 * Order by the assigned Comparator, or fall-back to default Comparator
 * for default-order.
 *
 * @extends ArrayList
 */
@SuppressWarnings("ALL")
public final class SortedList<E> extends ArrayList<E> {

    // Assigned Comparator
    private Comparator<E> mComparator;

    /**
     * Constructor that takes a List-collection, and sorts the elements
     * based off Assigned Comparator-order, or Default Comparator-order.
     *
     * @param list
     */
    public SortedList(Collection<? extends E> list) {
        super(list);
        sort();
    }

    /**
     * Constructor to assign Comparator for desired ordering.
     *
     * @param comparator
     */
    public SortedList(Comparator<E> comparator) {
        setComparator(comparator);
    }

    /**
     * Constructor that takes List-collection, as well as Comparator
     * to re-order the elements.
     *
     * @param list
     * @param comparator
     */
    public SortedList(Collection<? extends E> list, Comparator<E> comparator) {
        this(list);
        setComparator(comparator);
    }

    /**
     * Add Element.
     *
     * @param e
     * @return true
     */
    @Override
    public boolean add(E e) {
        boolean returnValue = super.add(e);
        sort();
        return returnValue;
    }

    /**
     * Add a List-collection.
     *
     * @param c
     * @return true
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean returnValue = super.addAll(c);
        sort();
        return returnValue;
    }

    /**
     * Assign Comparator for re-ordering.
     *
     * @param comparator
     */
    private void setComparator(Comparator<E> comparator) {
        mComparator = comparator;
        sort();
    }

    // Re-order elements.
    private void sort() {
        Collections.sort(this, mComparator);
    }
}
