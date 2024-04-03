package org.example;


import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class ImageMaker {
    private final int[][] imageMatrix;
    public ImageMaker(int[][] imageMatrix) {
        this.imageMatrix = imageMatrix;
    }

    public Image createImage() {
        int width = imageMatrix[0].length;
        int height = imageMatrix.length;

        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        int[] pixels = new int[width * height];

        for (int i = 0; i < height; i++) {
            System.arraycopy(imageMatrix[i], 0, pixels, i * width, width);
        }
        pixelWriter.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);
        return writableImage;
    }
}
