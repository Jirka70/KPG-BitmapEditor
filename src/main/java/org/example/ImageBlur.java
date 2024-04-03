package org.example;

public class ImageBlur {
    private final int[][] blurringImage;

    public ImageBlur(int[][] image) {
        blurringImage = image;
    }

    public void blurImage(int blurIntensity) {
        int[][] auxMatrix = copyMatrix();
        for (int i = 0; i < blurringImage.length; i++) {
            for (int j = 0; j < blurringImage[0].length; j++) {
                int averageColorForPixel = calculateAverageColorForPixel(auxMatrix, blurIntensity, j, i);
                blurringImage[i][j] = averageColorForPixel;
            }
        }
    }

    private int calculateAverageColorForPixel(int[][] auxMatrix, int blurIntensity,
                                              int currentPixelX, int currentPixelY) {
        int numberOfSurroundingPixels = (blurIntensity * 2) * (blurIntensity * 2);
        int[] surroundingColors = new int[numberOfSurroundingPixels];
        int iterator = 0;
        for (int y = -blurIntensity; y < blurIntensity; y++) {
            for (int x = -blurIntensity; x < blurIntensity; x++) {

                int surroundingPixelX = currentPixelX + x;
                int surroundingPixelY = currentPixelY + y;
                int surroundingPixel;
                if (surroundingPixelX < 0 || surroundingPixelX >= auxMatrix[0].length || surroundingPixelY < 0 || surroundingPixelY >= auxMatrix.length) {
                    surroundingPixel = 0;
                } else {
                    surroundingPixel = auxMatrix[surroundingPixelY][surroundingPixelX];
                }
                surroundingColors[iterator] = surroundingPixel;
                iterator++;

            }
        }

        int red = 0;
        int green = 0;
        int blue = 0;
        int alpha = 0;
        int numberOfValidPixels = 0;
        for (int surroundingColor : surroundingColors) {
            if (surroundingColor == 0) {
                continue;
            }
            red += (surroundingColor >> 16) & 0x000000FF;
            green += (surroundingColor >> 8) & 0x000000FF;
            blue += surroundingColor & 0x000000FF;
            alpha += (surroundingColor >> 24) & 0x000000FF;
            numberOfValidPixels++;
        }
        numberOfValidPixels = numberOfValidPixels == 0 ? numberOfSurroundingPixels : numberOfValidPixels;

        red /= numberOfValidPixels;
        green /= numberOfValidPixels;
        blue /= numberOfValidPixels;
        alpha /= numberOfValidPixels;

        return (alpha << 24) + (red << 16) + (green << 8) + blue;
    }

    private int[][] copyMatrix() {
        int[][] copiedMatrix = new int[blurringImage.length][blurringImage[0].length];
        for (int i = 0; i < copiedMatrix.length; i++) {
            System.arraycopy(blurringImage[i], 0, copiedMatrix[i], 0, copiedMatrix[0].length);
        }
        return copiedMatrix;
    }

    public void blurImageLocally(int x, int y) {
        int[][] auxMatrix = copyMatrix();
        int blurWidth = 100;
        int beginX = x - blurWidth / 2;
        int beginY = y - blurWidth / 2;
        int endX = x + blurWidth / 2;
        int endY = y + blurWidth / 2;
        for (int i = beginY; i < endY; i++) {
            for (int j = beginX; j < endX; j++) {
                if (j < 0 || j >= blurringImage[0].length || i < 0 || i >= blurringImage.length) {
                    continue;
                }
                int averagePixelValue = calculateAverageColorForPixel(auxMatrix, 10, j, i);
                blurringImage[i][j] = averagePixelValue;
            }
        }
    }

    private double calculateEuclideanDistance(double startX, double startY, double endX, double endY) {
        return Math.sqrt((endX - startX) * (endX - startX) + (endY - startY) * (endY - startY));
    }
}
