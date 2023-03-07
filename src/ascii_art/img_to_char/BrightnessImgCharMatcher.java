package ascii_art.img_to_char;

import image.Image;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The class that transfers the image to an ascii art.
 */
public class BrightnessImgCharMatcher {
    private static final int DEFAULT_PIXELS = 16; //The default in this program
    private static final double RED_GREY_RATIO = 0.2126;
    private static final double GREEN_GREY_RATIO = 0.7152;
    private static final double BLUE_GREY_RATIO = 0.0722;
    private static final int MAX_RGB_VAL = 255;
    private final HashMap<Character, Float> charsBrightness;
    private final Map<Character, Float> brightnessMapMatcher;
    private final Image img;
    private final String font;

    /**
     * The constructor of this class
     * @param img the image that will be transformed to an ascii art
     * @param font the font for the ascii art
     */
    public BrightnessImgCharMatcher(Image img, String font) {
        this.img = img;
        this.font = font;
        this.brightnessMapMatcher = new HashMap<>();
        this.charsBrightness = new HashMap<>();
    }

    /**
     * The method the gets a set of chars and create the 2D array of chars (from the picture).
     * @param numCharsInRow The number of chars in each row
     * @param charset the set of the chars
     * @return The image as an ascii art - 2D array
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charset) {
        if (charset.length != 0) {
            this.brightnessMapMatcher.clear(); //Deletes the past characters
            charSetAdder(charset);
        }
        int sizeSubs = this.img.getWidth()/numCharsInRow;

        char[][] charsImg = new char[this.img.getHeight()/sizeSubs][numCharsInRow];
        var subImages = this.img.subImages(sizeSubs).iterator();

        for (int row = 0; row < charsImg.length; row++) {
            for (int col = 0; col < charsImg[0].length; col++) {
                charsImg[row][col] = subImgToChar(subImages.next(), charset[0]);
            }
        }
        return charsImg;
    }

    /**
     * The method that fits the best character to every sub image
     * @param img the sub image
     * @param firstCharInSet the first char of the set (for first calculation)
     * @return the character that fits the most for the image
     */
    private char subImgToChar(Image img, Character firstCharInSet)
    {
        float subVal = greyValueSummer(img) / (img.getWidth()*img.getHeight());
        subVal /= MAX_RGB_VAL;

        char c = firstCharInSet;

        for (Character character : this.brightnessMapMatcher.keySet())
        {
            c = (Math.abs(this.brightnessMapMatcher.get(c) - subVal) >
                    Math.abs(this.brightnessMapMatcher.get(character) - subVal)) ? character : c;
        }
        return c;
    }

     /**
      * Method that returns the sum of all grey values in sub image
      * @param img the sub image
      * @return the sum of all greys values
      */
    private static float greyValueSummer(Image img)
    {
        float summer = 0;
        for (int row = 0; row < img.getHeight(); row++) {
            for (int col = 0; col < img.getWidth(); col++) {
                summer+= greyValue(img.getPixel(col,row));
            }
        }
        return summer;
    }
     /**
      * Function that calculate the grey value of the pixel.
      * @param color the color to transfer to grey
      * @return the grey value of the color
      */
    private static float greyValue(Color color)
    {
        return (float) (color.getRed()* RED_GREY_RATIO + color.getGreen()* GREEN_GREY_RATIO +
                        color.getBlue() * BLUE_GREY_RATIO);
    }
     /**
      * Method that normalize the map values (with the max and min values in the map).
      */
    private void normalizeMap()
    {
        float minVal = 1;
        float maxVal = 0;

        for (float value : this.brightnessMapMatcher.values())
        {
            maxVal = Math.max(value, maxVal);
            minVal = Math.min(value, minVal);
        }

        for (Character key : this.brightnessMapMatcher.keySet())
        {
            float curVal = this.brightnessMapMatcher.get(key);
            float newVal = (curVal - minVal) / (maxVal-minVal);
            this.brightnessMapMatcher.put(key, newVal);
        }
    }
     /**
      * Method that adds all characters in the set
      * @param characters set of characters.
      */
    public void charSetAdder(Character[] characters)
    {
        for (Character character : characters) {

            if (this.charsBrightness.get(character) == null) //Checks if the character is already in the map
            {
                boolean[][] booleanTable = CharRenderer.getImg(character, DEFAULT_PIXELS, this.font);
                this.charsBrightness.put(character,brightnessByWhitesNormalized(booleanTable));
            }

            this.brightnessMapMatcher.put(character, this.charsBrightness.get(character));
        }
        normalizeMap();
    }
     /**
      * Function that normalize the number to be between 0 to 1
      * @param blackWhiteImg the table to count the true cells
      * @return white cells/ DEFUALT PIXEL
      */
    private static float brightnessByWhitesNormalized (boolean[][] blackWhiteImg)
    {
        int whiteCounter = 0;
        for (int i = 0; i < blackWhiteImg.length; i++) {
            for (int j = 0; j < blackWhiteImg[i].length; j++) {
                if (blackWhiteImg[i][j])
                {
                    whiteCounter++;
                }
            }
        }

        return (float) whiteCounter/(blackWhiteImg.length*blackWhiteImg[0].length);
    }



}
