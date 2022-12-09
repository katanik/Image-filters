package com.company.filters;

import com.company.GUI;
import com.company.RGB;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static java.lang.Double.min;
import static java.lang.StrictMath.max;

public abstract class GaussFilter {
    private static int sigma = 2;

    GaussFilter() {
        buildKernel();
    }

    boolean readingSigma(String text) {
        if (GUI.isInteger(text)) {
            int newSigma = Integer.parseInt(text);
            if (newSigma > 0) {
                this.sigma = newSigma;
                return true;
            }
        }
        GUI.message.setText("Input correct sigma.");
        return false;
    }
    private double kernel[][];
    private int N;

    public abstract boolean readingParameters();

    private void buildKernel() {
        N = 3 * sigma; if (N%2==1) N++;
        kernel = new double[N][N];
        double div = 0, p = sigma * sigma * 2.;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double r = Math.pow(N / 2 - i, 2.) + Math.pow(N / 2 - j, 2.);
                kernel[i][j] = Math.exp(-r / p) / p / Math.PI;
                div += kernel[i][j];
            }
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                kernel[i][j] /= div;
            }
        }
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
                    GUI.matrixForImage[x][y]= countNewRGB(x, y);
                    GUI.matrixForImage[x][y] = new RGB(max(0., min(255., GUI.matrixForImage[x][y].getR())), max(0., min(255., GUI.matrixForImage[x][y].getG())), max(0., min(255., GUI.matrixForImage[x][y].getB())));
                }
            }

            GUI.repaintLastChange(name(), successMessage());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RGB countNewRGB(int X, int Y) {
        RGB newRGB= new RGB(0);
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

                newRGB = newRGB.addRGB(rgb.multiplyDouble(kernel[ii][jj]));
            }
        }
        return newRGB;
    }

    private String successMessage() {
        return "Gaussian filter has been successfully applied, sigma = " + Integer.toString(sigma) + ".";
    }

    private String name() {
        return "gauss_filter";
    }

    public static int getSigma(){return sigma;}

    public String outKernel() {
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
