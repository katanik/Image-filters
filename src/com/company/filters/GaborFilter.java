package com.company.filters;

import com.company.GUI;
import com.company.RGB;
import com.company.hsv_lab.Lab;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static java.lang.Double.min;
import static java.lang.StrictMath.max;

public abstract class GaborFilter{
    private double kernel[][];
    private int N;
    private static double theta=0.;
    private double lambda = 2., gamma = 1.;

    GaborFilter() {
        buildKernel();
    }

    private void buildKernel() {
        double p = 2. * Math.PI / lambda, p1 = -2. * Math.pow(0.56 * lambda, 2.);
        double psi = Math.PI / 3.8;
        N = 5;
        kernel = new double[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double x = i - N / 2, y = j - N / 2;
                double x1 = x * Math.cos(theta) + y * Math.sin(theta), y1 = -x * Math.sin(theta) + y * Math.cos(theta);
                kernel[i][j] = Math.exp((x1 * x1 + Math.pow(gamma * y1, 2.)) / p1) * Math.cos(p * x1 + psi);
            }
        }
    }

    protected abstract boolean readingParameters();

    static boolean readingTheta(String text) {
        if (GUI.isInteger(text)) {
            int newTheta = Integer.parseInt(text);
            if (newTheta >= 0) {
                if (theta>=360) theta-=360;
                theta = getRadianFromDegree((double)newTheta);
                return true;
            }
        }
        GUI.message.setText("Input correct theta.");
        return false;
    }

    private static double getRadianFromDegree(double angle) {
        return angle * Math.PI / 180.;
    }

    public void applyKernal() {
        if (!GUI.isImageSelected) {
            GUI.message.setText("Select image.");
            return;
        }

        if (!readingParameters()) return;

        if (!GUI.lastChange.equals(name()))
            GUI.overwritingSavedChange();

        try {
            GUI.image.setImage(GUI.pathSavedChange);
            GUI.bufferedImage = ImageIO.read(new File(GUI.pathSavedChange));
            GUI.matrixForImage = new RGB[GUI.bufferedImage.getWidth()][GUI.bufferedImage.getHeight()];


            for (int x = 0; x < GUI.bufferedImage.getWidth(); x++) {
                for (int y = 0; y < GUI.bufferedImage.getHeight(); y++) {
                    GUI.matrixForImage[x][y]=countNewRGB(x, y);
                    //GUI.matrixForImage[x][y] = new RGB(max(0., min(255., GUI.matrixForImage[x][y].getR())), max(0., min(255., GUI.matrixForImage[x][y].getG())), max(0., min(255., GUI.matrixForImage[x][y].getB())));
                }
            }

            GUI.repaintLastChange(name(), successMessage());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RGB countNewRGB(int X, int Y) {
        double value = 0;
        int x, y;
        for (int i = X - N / 2, ii = 0; ii < N; i++, ii++) {
            for (int j = Y - N / 2, jj = 0; jj < N; j++, jj++) {
                x=i; y=j;
                if (!(i >= 0 && j >= 0 && i < GUI.bufferedImage.getWidth() && j < GUI.bufferedImage.getHeight())){
                    if(x<0) x=0;
                    if(x>=GUI.bufferedImage.getWidth()) x=GUI.bufferedImage.getWidth()-1;

                    if(y<0) y=0;
                    if(y>=GUI.bufferedImage.getHeight()) y=GUI.bufferedImage.getHeight()-1;
                }
                RGB rgb = new RGB(GUI.bufferedImage.getRGB(x, y));
                value += new Lab(rgb).getL() * kernel[ii][jj];
            }
        }
        return new RGB(value, value, value);
    }

    private String successMessage() {
        return "Gabor filter has been successfully applied, theta = " + Integer.toString((int)(theta*180./Math.PI)) + ", lambda = " + Double.toString(lambda) + ", gamma = " + Double.toString(gamma)+".";
    }

    private String name() {
        return "gabor_filter";
    }

    public String outKernal() {
        String text = "";
        for (int x = 0; x < N; x++) {
            for (int y = 0; y < N; y++) {
                text += "          " + Double.toString(kernel[x][y]);
            }
            text += "\n";
        }
        return text;
    }
}
