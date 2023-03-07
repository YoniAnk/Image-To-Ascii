package image;

import java.awt.*;

/**
 * A package private class that is a sub image from a big images, it's implements Image
 */
class SubImage implements Image{
    private final Color[][] pixelArray;
    private final Image bigImage;
    private final int width;
    private final int height;
    private final int xInBigImg;
    private final int yInBigImg;

    /**
     * The constructor of the sub image
     * @param bigImage the image that this object will be a sub image of it
     * @param width the width of the sub image
     * @param height the height of the sub image
     * @param xStart the x coordinate to start taking the sub image from the big image
     * @param yStart the y coordinate to start taking the sub image from the big image
     */
    public SubImage(Image bigImage, int width, int height, int xStart, int yStart)
            throws IllegalArgumentException {
        this.pixelArray = new Color[height][width];
        this.bigImage = bigImage;
        this.width = width;
        this.height = height;
        this.xInBigImg = xStart;
        this.yInBigImg = yStart;

        if (xStart + width > this.bigImage.getWidth() || yStart + height > this.bigImage.getHeight() ||
        xStart < 0 || yStart < 0)
        {
            throw new IllegalArgumentException("values must match the big image");
        }
        this.subCreator();
    }

    /**
     * A method that creates the sub image from the big image
     */
    private void subCreator()
    {
        for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
                this.pixelArray[row][col] = this.bigImage.getPixel(xInBigImg+col,yInBigImg+row);
            }
        }
    }

    /**
     * Returns the pixel in the x,y coordinate
     * @param x the x coordinate of the pixel
     * @param y the y coordinate of the pixel
     * @return the color in the coordinate
     */
    @Override
    public Color getPixel(int x, int y) {
        return this.pixelArray[y][x];
    }

    /**
     * Getter for the width of the image
     * @return the width of the image
     */
    @Override
    public int getWidth() {
        return this.width;
    }

    /**
     * Getter for the height of the image
     * @return the width of the image
     */
    @Override
    public int getHeight() {
        return this.height;
    }
}
