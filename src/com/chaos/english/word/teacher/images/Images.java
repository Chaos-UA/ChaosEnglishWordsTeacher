package com.chaos.english.word.teacher.images;


import javax.swing.*;
import java.awt.*;

public class Images {
    public static javafx.scene.image.Image FX_MAIN_64x64 = new javafx.scene.image.Image(getImagePath("main_frame_64x64.png"));
    public static ImageIcon MAIN_64x64 = new ImageIcon(Images.class.getResource("main_frame_64x64.png"));

    public static String getImagePath(String imageName) {
        return Images.class.getResource(imageName).toString();
    }


    /**
     * @param newWidth if newWidth == -1 then proportional scale to height
     * @param newHeight if newHeight == -1 then proportional scale to width
     * @param image to scale
     * @return scaled ImageIcon
     */
    public static ImageIcon getScaledImageIcon(int newWidth, int newHeight, Image image) {

        if (image == null || newWidth == 0 || newHeight == 0 || newWidth < 0 && newHeight < 0)
            return null;
        //throw new IllegalArgumentException("newWidth == 0 || newHeight == 0 || newWidth < 0 && newHeight < 0");

        if (newWidth == -1 || newHeight == -1) {
            int originalWidth = image.getWidth(null);
            int originalHeight = image.getHeight(null);
            if (originalHeight < 1 || originalWidth < 1) throw new IllegalArgumentException("Error in Chaos.getScalledImageIcon");

            if (newHeight == - 1)
                newHeight = Math.max((int)( (float)originalHeight * ((float)newWidth / originalWidth) ), 1);
            else
                newWidth = Math.max((int) (originalWidth * ((float) newHeight / originalHeight)), 1);
        }

        return new ImageIcon(image.getScaledInstance(newWidth,newHeight,Image.SCALE_SMOOTH));
    }
}
