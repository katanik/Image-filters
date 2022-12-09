package com.company.segmentation;

import com.company.GUI;
import com.company.RGB;
import com.company.hsv_lab.Lab;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import static com.company.segmentation.SegmentationPanel.colorDistance;

public class SplitAndMerge /*extends JFrame*/ {
    private int W, H, N;

    SplitAndMerge() {}

    void applySplitAndMerge(){
        /* super();
        this.setVisible(true);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage("src\\icon.png"));
        this.setBackground(GUI.cuteColor.darker());
        */
        BufferedImage bImage = GUI.bufferedImage;
        W = bImage.getWidth();
        H = bImage.getHeight();
        N = W * H;

        buildSet(N);
        split(0, 0, W, H, Math.min(W, H));
        merge();

        drawSegments(bImage);
    }

    private void drawSegments(BufferedImage bImage) {
       for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                cl[x + y * W] = randomColor();
            }
        }

        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H; y++) {
                bImage.setRGB(x, y, cl[getSet(y * W + x)]);
            }
        }

        try {
            ImageIO.write(bImage, "jpg", new File(".\\split_and_merge.jpg"));
            /*ImagePanel img = new ImagePanel();
            img.screenSizeTransfer(1000, 570, 0, 0);
            // JLabel label = new JLabel();
            // imageSegmentation.add(new JLabel(new ImageIcon(bImage)));
            img.setBackground(GUI.cuteColor.darker());
            img.setImage(".\\split_and_merge.jpg");
            this.setBounds(205, 0, img.getWidthPanel(), img.getHeightPanel());
            this.add(img);*/
            GUI.image.setImage(".\\split_and_merge.jpg");
            GUI.message.setText("Split and merge algorithm applied, CIEDE2000 between two colors < " + Double.toString(colorDistance) + ".");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int parents[];
    private int cl[];
    private int rang[];

    private int randomColor() {
        return (new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255), 10)).getRGB();
    }

    private void buildSet(int n) {
        parents = new int[n];
        rang = new int[n];
        cl = new int[n];

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                int i = x + y * W;
                parents[i] = i;
                rang[i] = 0;
                cl[i] = GUI.bufferedImage.getRGB(x, y);
            }
        }
    }

    private int getSet(int v) {
        return (v == parents[v]) ? v : (parents[v] = getSet(parents[v]));
    }

    private void unionSet(int a, int b) {
        a = getSet(a);
        b = getSet(b);
        if (a != b) {
            if (rang[a] < rang[b]) {
                int p = a;
                a = b;
                b = p;
            }
            parents[b] = a;
            if (rang[a] == rang[b])
                rang[a]++;
        }
    }

    private class Edge {
        private int u, v;
        private double cost;

        Edge(int u, int v, double cost) {
            this.u = u;
            this.v = v;
            this.cost = cost;
        }
    }

    private void setSelf(int x1, int y1, int x2, int y2) {
        int w = x2 - x1;
        int d[] = {-W + w - 1, -1, W - w + 1, 1};

        unionSet(x1 + y1 * W, x1 + y1 * W + d[2]);
        unionSet(x1 + y1 * W, x1 + y1 * W + d[3]);

        unionSet(x1 + y2 * W, x1 + y2 * W + d[0]);
        unionSet(x1 + y2 * W, x1 + y2 * W + d[3]);

        unionSet(x2 + y1 * W, x2 + y1 * W + d[1]);
        unionSet(x2 + y1 * W, x2 + y1 * W + d[2]);

        unionSet(x2 + y2 * W, x2 + y2 * W + d[0]);
        unionSet(x2 + y2 * W, x2 + y2 * W + d[1]);

        for (int y = y1 + 1; y < y2 - 1; y++) {
            for (int x = x1 + 1; x < x2 - 1; x++) {
                int i = y * W + x;
                if (i >= N) return;
                for (int k = 0; k < 4; k++) {
                    int j = i + d[k];
                    if (j >= 0 && j < N)
                        unionSet(i, j);
                }
            }
        }
    }

    private void merge() {
        int d[] = {-W, -1, W, 1};

      //  ArrayList<Edge> edges = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            for (int k = 0; k < 4; k++) {
                int j = i + d[k];
                if (j >= 0 && j < N && !(i % W == 0 && k == 1) && !(i % W == W - 1 && k == 3) && !(i < W && k == 0) && !(i >= N - W && k == 2)) {
                    //double dist = CIEDE2000(cl[getSet(i)], cl[getSet(j)]);
                    double dist = CIEDE2000(cl[i], cl[j]);
                    if (dist < colorDistance) {
                        unionSet(i, j);
                        //edges.add(new Edge(i, j, dist));
                    }
                }
            }
        }


        /*edges.sort(new Comparator<Edge>() {
            @Override
            public int compare(Edge e1, Edge e2) {
                return Double.compare(e1.cost, e2.cost);
            }
        });

        for (int i = 0; i < edges.size(); i++) {
            int a = edges.get(i).u;
            int b = edges.get(i).v;
            unionSet(a, b);
        }
        edges = null;*/
    }

    private void split(int x1, int y1, int x2, int y2, int r) {
        if (x2 - x1 <= 1 && y2 - y1 <= 1 || isSelf(x1, y1, x2, y2)) {
            setSelf(x1, y1, x2, y2);
            return;
        }

        int minW, minH = 0, newr = r / 2 + 1;
        for (int y = y1; y < y2; y += r) {
            for (int x = x1; x < x2; x += r) {
                minW = Math.min(x + r - 1, x2);
                minH = Math.min(y + r - 1, y2);
                //if (SegmentationPanel.isSelf(x, y, minW, minH))
                //    setSelf(x, y, minW, minH);
                //else
                split(x, y, minW, minH, newr);

            }
        }
    }

    private double CIEDE2000(int x1, int y1, int x2, int y2) {
        Lab lab1 = new Lab(new RGB(GUI.bufferedImage.getRGB(x1, y1)));
        Lab lab2 = new Lab(new RGB(GUI.bufferedImage.getRGB(x2, y2)));
        //return Math.sqrt(Math.pow((lab1.getL()-lab2.getL()), 2.)+Math.pow((lab1.geta()-lab2.geta()), 2.)+Math.pow((lab1.getb()-lab2.getb()), 2.));
        return lab1.CIEDE2000(lab2);
    }

    private double CIEDE2000(int a, int b) {
        Lab lab1 = new Lab(new RGB(a));
        Lab lab2 = new Lab(new RGB(b));
        //return Math.sqrt(Math.pow((lab1.getL()-lab2.getL()), 2.)+Math.pow((lab1.geta()-lab2.geta()), 2.)+Math.pow((lab1.getb()-lab2.getb()), 2.));
        return lab1.CIEDE2000(lab2);
    }

    private boolean isSelf(int x1, int y1, int x2, int y2) {
        if (x2 - x1 <= 1 && y2 - y1 <= 1) return true;
        double dist = colorDistance, error = 0.7;
        int xc = (x1 + x2) / 2, yc = (y1 + y2) / 2;
        int kol = 0;

        for (int x = x1 + 1; x < x2; x++) {
            for (int y = y1 + 1; y < y2; y++) {
                int xrandom = (int) (Math.random() * (double) (x2 - x1) + (double) x1), yrandom = (int) (Math.random() * (double) (x2 - x1) + (double) x1);
                if (CIEDE2000(x, y, xc, yc) < dist && CIEDE2000(x, y, xrandom, yrandom) < dist) {
                    kol++;
                }
            }
        }
        return kol >= (double) ((x2 - x1) * (y2 - y1)) * error;
    }

}
