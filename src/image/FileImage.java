package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;


/**
 * A package-private class of the package image.
 * @author Dan Nirel
 */
class FileImage implements Image {
    private static final Color DEFAULT_COLOR = Color.WHITE;

    private final Color[][] pixelArray;

    public FileImage(String filename) throws IOException {
        java.awt.image.BufferedImage im = ImageIO.read(new File(filename));
        int origWidth = im.getWidth(), origHeight = im.getHeight();
        //im.getRGB(x, y)); getter for access to a specific RGB rates

        int addingToWidth = secondPowFinder(origWidth);
        int addingToHeight = secondPowFinder(origHeight);

        int newWidth = origWidth + addingToWidth;
        int newHeight = origHeight + addingToHeight;

        this.pixelArray = new Color[newHeight][newWidth];
        for (int row = 0; row < newHeight; row++) {
            for (int col = 0; col < newWidth; col++) {

                if ((row < addingToHeight/2) || (row > newHeight - (addingToHeight/2) - 1) ||
                        (col < addingToWidth/2) || (col > newWidth - (addingToWidth/2) - 1))
                {
                    this.pixelArray[row][col] = DEFAULT_COLOR;
                }
                else
                {
                    int rowFromOrig = row - (addingToHeight / 2);
                    int colFromOrig = col - (addingToWidth / 2);
                    pixelArray[row][col] = new Color(im.getRGB(colFromOrig, rowFromOrig));
                }
            }

        }
    }

    /**
     * Function that get a number and return the difference between the number and the closest (from top)
     * power of 2.
     * @param numberToCheck the number to be checked.
     * @return the difference between that number and the closest (from above) power of 2.
     */
    private static int secondPowFinder(int numberToCheck)
    {
        int base = 2;
        while (base < numberToCheck)
        {
            base *=2;
        }

        return (base-numberToCheck);

    }

    /**
     * Method that returns the width of the image
     * @return the width of the image
     */
    @Override
    public int getWidth() {
        return this.pixelArray[0].length;
    }

    /**
     * Method that returns the Height of the image
     * @return the height of the image
     */
    @Override
    public int getHeight() {
        return this.pixelArray.length;
    }

    /**
     * Method that return the Color int a specific pixel
     * @param x the x coordinate of the pixel
     * @param y the y coordinate of the pixel
     * @return the Color in this pixel.
     */
    @Override
    public Color getPixel(int x, int y) {
        return this.pixelArray[y][x];
    }
}
