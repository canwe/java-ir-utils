package ru.krondix.ir.sparse;

import no.uib.cipr.matrix.sparse.Arrays;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Memory-efficient sparse wrapper over ArrayList.
 * Stores only non-null elements.
 *
 * @author Egor Azanov, <a href="mailto:krondix@yandex-team.ru">
 */
public class SparseArrayList<T> implements Iterable<T> {
    private int[] indexes = new int[0];
    private List<T> data = new ArrayList<T>();
    private int used;

    public void set(int index, T value) {
        check(index);

        int realIndex = getIndex(index);
        data.set(realIndex, value);
    }

    public T get(int index) {
        check(index);

        int realIndex = Arrays.binarySearch(indexes, index, 0, data.size());
        if (realIndex >= 0) {
            return data.get(realIndex);
        }
        return null;
    }

    private int getIndex(int index) {
        int i = Arrays.binarySearchGreater(indexes, index, 0, data.size());

        if (i < used && indexes[i] == index) {
            return i;
        }

        int[] newIndexes = indexes;
        if (++used + 1 > indexes.length) {
            int newLength = data.size() != 0 ? data.size() << 1 : 1;

            newIndexes = new int[newLength];
            System.arraycopy(indexes, 0, newIndexes, 0, i);
        }

        try {
            System.arraycopy(indexes, i, newIndexes, i + 1, used - i - 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        newIndexes[i] = index;

        indexes = newIndexes;
        data.add(i, null);

        return i;
    }

    public Iterator<T> iterator() {
        return data.iterator();
    }

    protected void check(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("" + index);
        }
    }
}
