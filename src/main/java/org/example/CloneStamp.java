package org.example;

public class CloneStamp {
    private final int[][] matrix;
    private int[][] clonedMatrix = null;
    public CloneStamp(int[][] matrix) {
        this.matrix = matrix;
    }

    public void cloneSelectedArea(int startX, int startY, int endX, int endY) {

        int incrementY = endY > startY ? 1 : -1;
        int incrementX = endX > startX ? 1 : -1;

        clonedMatrix = new int[Math.abs(endY - startY)][Math.abs(endX - startX)];

        for (int i = startY; i < endY; i += incrementY) {
            for (int j = startX; j < endX; j += incrementX) {
                int y = Math.abs(i - startY);
                int x = Math.abs(j - startX);
                clonedMatrix[y][x] = matrix[i][j];
            }
        }
    }

    public void pasteSelectedArea(int x, int y) {
        if (clonedMatrix.length == 0) {
            return;
        }
        int toY = Math.min(matrix.length, y + clonedMatrix.length);
        int toX = Math.min(matrix[0].length, x + clonedMatrix[0].length);
        for (int i = y; i < toY; i++) {
            if (toX - x >= 0) {
                System.arraycopy(clonedMatrix[i - y], 0, matrix[i], x, toX - x);
            }
        }
    }
}
