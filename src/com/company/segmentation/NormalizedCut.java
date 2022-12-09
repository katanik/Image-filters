package com.company.segmentation;

import com.company.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NormalizedCut extends JFrame {
    private int colorDistance;

    public NormalizedCut() {
        super();
        this.colorDistance = colorDistance;
        this.setIconImage(Toolkit.getDefaultToolkit().getImage("src\\icon.png"));

        BufferedImage bImage = GUI.bufferedImage;

        GUI.message.setText("Normalized cut.");
        this.setVisible(true);
    }

    private double D[][], W[][];

    private void countMatrixD(){
        int w=GUI.bufferedImage.getWidth(), h=GUI.bufferedImage.getHeight();
        int n=w*h, dx[] = {-1, 0, 0, 1}, dy[] = {0, -1, 1, 0};
        D=new double[n][n];
        W=new double[n][n];
        for (int x=0; x<w; x++){
            for (int y=0; y<h; y++) {
                for (int i=0; i<4; i++) {
                    if (x+dx[i]>=0 && x+dx[i]<w && y+dy[i]>=0 && y+dy[i]<h) {
                        //W[x][y] = CIEDE2000(x, y, x+dx[i], y+dy[i]);
                    }
                }
            }
        }

    }
}
