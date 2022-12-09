package com.company.segmentation;

import com.company.GUI;
import com.company.RGB;
import com.company.hsv_lab.Lab;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.company.GUI.bufferedImage;
import static com.company.segmentation.SegmentationPanel.colorDistance;
import static com.company.segmentation.SegmentationPanel.numberOfIterations;

public class MeanShift {
    private int width, height, N, radius = 3;
    static RGB colors[][];
    static int alpha[][];

    MeanShift() {
    }

    void applyMeanShift() {
        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();
        N = width * height;
        colors = new RGB[width][height];
        alpha = new int[width][height];
        for (int iteration = 1; iteration <= numberOfIterations; iteration++) {
            iterationMeanShift();
        }
        drawSegments();
    }

    private void drawSegments() {
        /*for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bImage.setRGB(x, y, colors[x][y].getIntRGB(alpha[x][y]));
            }
        }*/

        try {
            ImageIO.write(bufferedImage, "jpg", new File(".\\mean_shift.jpg"));
            GUI.image.setImage(".\\mean_shift.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        GUI.message.setText("Mean shift algorithm applied, CIEDE2000 between two colors < " + Double.toString(colorDistance) +
                ", number of iterations " + Integer.toString(numberOfIterations) + ".");
    }

    private double dist(int x1, int y1, int x2, int y2) {
        //return Math.abs(x1 - x2) + Math.abs(y1 - y2);
        return Math.sqrt(Math.pow(x1 - x2, 2.) + Math.pow(y1 - y2, 2.));
    }

    private void recordColor() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = bufferedImage.getRGB(x, y);
                colors[x][y] = new RGB(argb);
                alpha[x][y] = (argb >> 24) & 0xff;
            }
        }
    }

    private void iterationMeanShift() {
        recordColor();
        ArrayList<Integer> xNear = new ArrayList<>(), yNear = new ArrayList<>();
        RGB mean;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                xNear.clear();
                yNear.clear();
                mean = new RGB(0);
                for (int i = x - radius; i <= x + radius; i++) {
                    for (int j = y - radius; j <= y + radius; j++) {
                        if (i >= 0 && i < width && j >= 0 && j < height) {
                            if (dist(x, y, i, j) <= radius && CIEDE2000(x, y, i, j) < colorDistance) {
                                xNear.add(i);
                                yNear.add(j);
                                mean.add(colors[i][j]);
                            }
                        }
                    }
                }
                mean.multiply(1. / (double) xNear.size());
                int new_argb = mean.getIntRGB(alpha[x][y]);
                bufferedImage.setRGB(x, y,new_argb);
            }
        }
    }

    private double CIEDE2000(int x1, int y1, int x2, int y2) {
        Lab lab1 = new Lab(colors[x1][y1]);
        Lab lab2 = new Lab(colors[x2][y2]);
        //return Math.sqrt(Math.pow((lab1.getL()-lab2.getL()), 2.)+Math.pow((lab1.geta()-lab2.geta()), 2.)+Math.pow((lab1.getb()-lab2.getb()), 2.));
        return lab1.CIEDE2000(lab2);
    }
}
