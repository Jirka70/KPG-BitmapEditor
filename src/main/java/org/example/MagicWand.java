package org.example;

import java.util.*;

public class MagicWand {
    private final int[][] imageMatrix;

    public MagicWand(int[][] imageMatrix) {
        this.imageMatrix = imageMatrix;
    }

    /**
     * @return set of all pixels, which are similar to the clicked one
     */
    public Set<Pixel> findAllSimilarPixels(int x, int y) {
        Set<Pixel> exploredPixels = new HashSet<>();
        Queue<Pixel> pixelsToExplore = new LinkedList<>();
        int clickedPixelValue = imageMatrix[y][x];

        pixelsToExplore.add(new Pixel(clickedPixelValue, x, y));
        while (!pixelsToExplore.isEmpty()) {
            Pixel exploringPixel = pixelsToExplore.poll();
            if (exploredPixels.contains(exploringPixel)) {
                continue;
            }
            List<Pixel> adjacentPixels = collectAdjacentPixels(clickedPixelValue, exploringPixel, exploredPixels);
            pixelsToExplore.addAll(adjacentPixels);
            exploredPixels.add(exploringPixel);
            //System.out.println(exploringPixel);
        }

        return exploredPixels;
    }

    private List<Pixel> collectAdjacentPixels(int clickedPixelValue, Pixel exploringPixel, Set<Pixel> exploredPixels) {
        int x = exploringPixel.x;
        int y = exploringPixel.y;

        List<Pixel> adjacentPixels = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (x + j >= imageMatrix[0].length || x + j < 0 || y + i >= imageMatrix.length || y + i < 0) {
                    continue;
                }

                if (Math.abs(i) + Math.abs(j) == 2) {
                    continue;
                }

                if (i == 0 && j == 0) {
                    continue;
                }

                Pixel adjacentPixel = new Pixel(imageMatrix[i + y][j + x], j + x, y + i);
                if (exploredPixels.contains(adjacentPixel)) {
                    continue;
                }

                if (!areTwoPixelsSimilar(clickedPixelValue, adjacentPixel.value)) {
                    continue;
                }

                adjacentPixels.add(adjacentPixel);
            }
        }

        return adjacentPixels;
    }

    public static boolean areTwoPixelsSimilar(int pixel1Value, int pixel2Value) {
        final int TOLERANCE = 15;
        int alpha1 = (pixel1Value >> 24) & 0x000000FF;
        int alpha2 = (pixel2Value >> 24) & 0x000000FF;

        int red1 = (pixel1Value >> 16) & 0x000000FF;
        int red2 = (pixel2Value >> 16) & 0x000000FF;

        int green1 = (pixel1Value >> 8) & 0x000000FF;
        int green2 = (pixel2Value >> 8) & 0x000000FF;

        int blue1 = pixel1Value & 0x000000FF;
        int blue2 = pixel2Value & 0x000000FF;

        double difference = Math.pow(Math.pow(alpha1 - alpha2, 4) + Math.pow(red1 - red2, 4) + Math.pow(green1 - green2, 4) + Math.pow(blue1 - blue2, 4), 0.25);

        return difference < TOLERANCE;
    }

    public static class Pixel {
        public final int value;
        public final int x;
        public final int y;

        public Pixel(int value, int x, int y) {
            this.value = value;
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pixel pixel)) return false;
            return value == pixel.value && x == pixel.x && y == pixel.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, x, y);
        }

        @Override
        public String toString() {
            return "Pixel{" +
                    "value=" + value +
                    ", x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}
