package image;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

/**
 * A package-private class of the package image.
 * @author Dan Nirel
 */
class ImageIterableProperty<T> implements Iterable<T> {
    public static final int DEFAULT_ADVANCE = 1;
    private final Image img;
    private final BiFunction<Integer, Integer, T> propertySupplier;
    private final int advanceInX;
    private final int advanceInY;

    /**
     * The constructor of the Iterable property
     * @param img the image for the iterator
     * @param propertySupplier the function that the iterator will apply every iteration
     */
    public ImageIterableProperty(
            Image img,
            BiFunction<Integer, Integer, T> propertySupplier) {
        this.img = img;
        this.propertySupplier = propertySupplier;
        this.advanceInX = DEFAULT_ADVANCE;
        this.advanceInY = DEFAULT_ADVANCE;
    }

    /**
     * The constructor of the Iterable property - (The new constructor)
     * @param img the image to iterate over
     * @param propertySupplier the function that the iterator will apply every iteration
     * @param advanceInX how many pixels to advance every iteration in x coordinate
     * @param advanceInY how many pixels to advance every iteration in y coordinate
     */
    public ImageIterableProperty(
            Image img,
            BiFunction<Integer, Integer, T> propertySupplier,
            int advanceInX, int advanceInY)
    {
        this.img = img;
        this.propertySupplier = propertySupplier;

        this.advanceInX = advanceInX;
        this.advanceInY = advanceInY;
    }

    /**
     * The iterator method
     * @return An iterator
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int x = 0, y = 0;

            /**
             * Method that checks if there is another iteration
             * @return yes if there are more items and false if not
             */
            @Override
            public boolean hasNext() {
                return (y < img.getHeight());
            }

            /**
             * Method that returns the nest item in the iterator
             * @return T object, the next item in the iterator.
             */
            @Override
            public T next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                var next = propertySupplier.apply(x, y);
                x += advanceInX;
                if (x >= img.getWidth()) {
                    x = 0;
                    y += advanceInY;
                }
                return next;
            }
        };
    }
}
