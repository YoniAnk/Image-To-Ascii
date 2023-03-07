package image;

import java.awt.*;
import java.io.IOException;

/**
 * Facade for the image module and an interface representing an image.
 * @author Dan Nirel
 */
public interface Image {
    /**
     * Returns the pixel in the x,y coordinate
     * @param x the x coordinate of the pixel
     * @param y the y coordinate of the pixel
     * @return the color in the coordinate
     */
    Color getPixel(int x, int y);

    /**
     * Getter for the width of the image
     * @return the width of the image
     */
    int getWidth();

    /**
     * Getter for the height of the image
     * @return the width of the image
     */
    int getHeight();

    /**
     * Open an image from file. Each dimensions of the returned image is guaranteed
     * to be a power of 2, but the dimensions may be different.
     * @param filename a path to an image file on disk
     * @return an object implementing Image if the operation was successful,
     * null otherwise
     */
    static Image fromFile(String filename) {
        try {
            return new FileImage(filename);
        } catch(IOException ioe) {
            return null;
        }
    }

    /**
     * Allows iterating the pixels' colors by order (first row, second row and so on).
     * @return an Iterable<Color> that can be traversed with a foreach loop
     */
    default Iterable<Color> pixels() {
        return new ImageIterableProperty<>(
                this, this::getPixel);
    }

    /**
     * Allows iterating the sub images by order (first row, second row and so on), the size of the sun image
     * is subSize x subSize.
     * @param subSize the size of the sub image
     * @return an Iterable<Image> that can be traversed with a foreach loop
     */
    default Iterable<Image> subImages(int subSize){
        return new ImageIterableProperty<>(this,
                (x, y) -> new SubImage(this, subSize, subSize, x, y),
                subSize, subSize);
    }
}
