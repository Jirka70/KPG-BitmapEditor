package org.example;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageReader {
    private final String imageFile;
    public ImageReader(String imageFile) {
        this.imageFile = imageFile;
    }

    public int[][] loadImage() throws IOException {
        Path imageFilePath = Paths.get(imageFile);
        try (InputStream in = Files.newInputStream(imageFilePath)) {
            BufferedImage bufferedImage = ImageIO.read(in);
            return createImageMatrix(bufferedImage);
        }
    }

    private int[][] createImageMatrix(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[][] imageMatrix = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                imageMatrix[i][j] = bufferedImage.getRGB(j, i);
            }
        }

        return imageMatrix;
    }
}

