package com.company.hsv_lab;

import com.company.*;
import com.company.panel_elements.ButtonForPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HSVLabPanel extends JPanel{
    public HSVLabPanel(){
        super();
        this.setBackground(GUI.cuteColor);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBounds(GUI.dimensionsForChangePanel);

        insertSlidersForHSV();
        insertButtonsForLab();
    }

    public void resetSliders(){
        sliderH.setValue(0);
        sliderS.setValue(0);
        sliderV.setValue(0);
    }

    // добавляем кнопки для отображения гистограммы
    private void insertButtonsForLab(){
        char types[] = {'L', 'a', 'b'};
        for (int i=0; i<3; i++) {
            char type=types[i];
            this.add(new JLabel("<html><br><br></html>"));
            this.add(new ButtonForPanel(0, "☆* Show histogram for "+type+"*☆") {
                @Override
                public void buttonAction() {
                    if (GUI.isImageSelected)
                        buildHistogram(type);
                    else
                        GUI.message.setText("Select image.");
                }
            });
        }
    }

    // добавление ползунков для изменения hsv
    private void insertSlidersForHSV() {
        this.add(new JLabel("<html><br>Hue<br><br></html>"));
        this.add(sliderH = new Slider(-360, 360, 0, 'H', 180));

        this.add(new JLabel("<html><br><br>Saturation<br><br></html>"));
        this.add(sliderS = new Slider(-100, 100, 0, 'S', 50));

        this.add(new JLabel("<html><br><br>Value<br><br></html>"));
        this.add(sliderV = new Slider(-100, 100, 0, 'V', 50));
        this.add(new JLabel("<html><br><br></html>"));
    }

    // изменить компоненту hsv с типом type на add для всего изображения
    private void changeHSVvalue(double add, char type) {
        if (GUI.lastChange != "hsv")
            GUI.overwritingSavedChange();

        try {
            //GUI.image.setImage(GUI.pathSavedChange);
            GUI.bufferedImage = ImageIO.read(new File(GUI.pathSavedChange));
            GUI.matrixForImage = new RGB[GUI.bufferedImage.getWidth()][GUI.bufferedImage.getHeight()];

            switch (type) {
                case 'H':
                    addH = add;
                    break;
                case 'S':
                    addS = add;
                    break;
                case 'V':
                    addV = add;
                    break;
            }

            RGB rgb = new RGB();
            HSV hsv = new HSV();
            int argb;
            for (int x = 0; x < GUI.bufferedImage.getWidth(); x++) {
                for (int y = 0; y < GUI.bufferedImage.getHeight(); y++) {
                    argb = new Color(GUI.bufferedImage.getRGB(x, y)).getRGB();
                    rgb.setRGB(argb);
                    hsv.setHSV(rgb);

                    // изменяем нужную величину hsv, учитывая предыдущие изменения
                    if (addH != 0) hsv.changeH(addH);
                    if (addS != 0) hsv.changeS(addS);
                    if (addV != 0) hsv.changeV(addV);

                    GUI.matrixForImage[x][y] = hsv.toRGB();
                }
            }

            GUI.normalizationImage();
            GUI.repaintLastChange("hsv", "change hue = " + Double.toString(addH) +
                    "\nchange saturation = " + Double.toString(addS) +
                    "\nchange value = " + Double.toString(addV));

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private class Slider extends JSlider {
        Slider(int min, int max, int value, char type, int tick) {
            super(JSlider.HORIZONTAL, min, max, value);
            setBackground(GUI.cuteColor);
            setForeground(Color.gray);
            setPreferredSize(new Dimension(60, 30));
            setMajorTickSpacing(tick);
            setMinorTickSpacing(tick);
            setPaintTicks(true);
            setPaintLabels(true);
            addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent event) {
                    //if (!getValueIsAdjusting()) {
                    int add = getValue();
                    changeHSVvalue(add, type);
                    //}
                }
            });
        }
    }

    // построение гистограммы в новом окне для соответствующей компоненты
    private void buildHistogram(char type) {
        try {
            BufferedImage bImage = ImageIO.read(new File(GUI.pathLastChange));

            int N = (bImage.getWidth() + 1) * (bImage.getHeight() + 1);
            double[] value = new double[N];
            HistogramDataset dataset = new HistogramDataset();
            dataset.setType(HistogramType.RELATIVE_FREQUENCY);

            // добавляем компоненты в value для всех пикселей
            int x, y, argb;
            RGB rgb = new RGB();
            Lab lab = new Lab(rgb);
            for (int i = 0; i < bImage.getWidth(); i++) {
                for (int j = 0; j < bImage.getHeight(); j++) {
                    x = i;
                    y = j;
                    argb = new Color(bImage.getRGB(x, y)).getRGB();
                    rgb.setRGB((argb >> 16) & 0xff, (argb >> 8) & 0xff, argb & 0xff);
                    lab.setLab(rgb);
                    double val = 0;
                    switch (type) {
                        case 'L':
                            val = lab.getL();
                            break;
                        case 'a':
                            val = lab.geta();
                            break;
                        case 'b':
                            val = lab.getb();
                            break;
                    }

                    value[i * bImage.getHeight() + j] = val;
                }
            }

            dataset.addSeries("Histogram", value, 100);

            JFreeChart chart = ChartFactory.createHistogram("", "Lightness", "Number",
                    dataset, PlotOrientation.VERTICAL, false, false, false);

            histogramDesign(chart, type);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // оформление окна с гистограммой
    private void histogramDesign(JFreeChart chart, char type){
        chart.setBackgroundPaint(GUI.cuteColor);
        chart.getTitle().setPaint(GUI.cuteColor.darker());
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);

        ChartFrame hist = new ChartFrame("Histogram for " + type + "-component", chart);

        hist.setIconImage(Toolkit.getDefaultToolkit().getImage("src\\icon.png"));
        hist.setBackground(Color.WHITE);
        hist.pack();
        hist.setLocationRelativeTo(null); //окно по центру
        hist.setVisible(true);
    }

    private double addH = 0, addS = 0, addV = 0;
    private Slider sliderH, sliderS, sliderV;
}


