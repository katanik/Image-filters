package com.company.filters;

import com.company.GUI;
import com.company.RGB;
import com.company.hsv_lab.HSV;
import com.company.hsv_lab.Lab;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static java.lang.Double.min;
import static java.lang.StrictMath.max;

public abstract class SobelFilter{
    SobelFilter() {
        buildKernel();
    }

    private void buildKernel() {
        N = 3;
        kernel = new double[3][3];
        kernel[0][0] = -1.;
        kernel[1][0] = -2.;
        kernel[2][0] = -1.;
        kernel[0][2] = 1.;
        kernel[1][2] = 2.;
        kernel[2][2] = 1.;

        kernelAdditional = new double[3][3];
        kernelAdditional[0][0] = -1.;
        kernelAdditional[0][1] = -2.;
        kernelAdditional[0][2] = -1.;
        kernelAdditional[2][0] = 1.;
        kernelAdditional[2][1] = 2.;
        kernelAdditional[2][2] = 1.;
    }

    private double kernel[][], kernelAdditional[][];
    private int N;

    protected abstract boolean readingParameters();

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
                    GUI.matrixForImage[x][y] = countNewRGB(x, y);
                    GUI.matrixForImage[x][y] = new RGB(max(0., min(255., GUI.matrixForImage[x][y].getR())), max(0., min(255., GUI.matrixForImage[x][y].getG())), max(0., min(255., GUI.matrixForImage[x][y].getB())));
                }
            }

            GUI.repaintLastChange(name(), successMessage());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RGB countNewRGB(int X, int Y) {
        RGB Gx = new RGB(0), Gy = new RGB(0);
        int x, y;
        for (int i = X - N / 2, ii = 0; ii < N; i++, ii++) {
            for (int j = Y - N / 2, jj = 0; jj < N; j++, jj++) {
                x = i;
                y = j;
                if (!(i >= 0 && j >= 0 && i < GUI.bufferedImage.getWidth() && j < GUI.bufferedImage.getHeight())) {
                    if (x < 0) x = 0;
                    if (x >= GUI.bufferedImage.getWidth()) x = GUI.bufferedImage.getWidth() - 1;

                    if (y < 0) y = 0;
                    if (y >= GUI.bufferedImage.getHeight()) y = GUI.bufferedImage.getHeight() - 1;
                }
                RGB rgb = new RGB(GUI.bufferedImage.getRGB(x, y));

                HSV hsv = new HSV(rgb);
                hsv.changeS(-100);
                rgb = hsv.toRGB();

                Gy = Gy.addRGB(rgb.multiplyDouble(kernel[ii][jj]));
                Gx = Gx.addRGB(rgb.multiplyDouble(kernelAdditional[ii][jj]));
            }
        }
        return (Gx.square().addRGB(Gy.square())).sqrt();
    }

    private String successMessage() {
        return "Sobel filter has been successfully applied.";
    }

    private String name() {
        return "sobel_filter";
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
