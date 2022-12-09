package com.company.segmentation;

import com.company.GUI;
import com.company.RGB;
import com.company.hsv_lab.Lab;
import com.company.panel_elements.ButtonForPanel;
import com.company.panel_elements.ParametrFieldForPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class SegmentationPanel extends JPanel {

    static double colorDistance = 1.5;
    static int numberOfIterations=1;
    private JTextField textColorDistance, textNumberOfIteration;

    public SegmentationPanel() {
        super();
        this.setBackground(GUI.cuteColor);
        this.setLayout(null);
        this.setBounds(GUI.dimensionsForChangePanel);

        this.add(new ParametrFieldForPanel(50, "color distance < ") {
            @Override
            public void inputParametrValue() {
                textColorDistance = new JTextField(Double.toString(colorDistance), 8);
                this.add(textColorDistance);
            }
        });

        this.add(new ButtonForPanel(150, "Split and merge") {
            @Override
            public void buttonAction() {
                applySegmentation("split_merge");
            }
        });

        this.add(new ButtonForPanel(250, "Normalized cut") {
            @Override
            public void buttonAction() {
                applySegmentation("normalized_cut");
            }
        });

        this.add(new ParametrFieldForPanel(320, "number of iterations = ") {
            @Override
            public void inputParametrValue() {
                textNumberOfIteration = new JTextField(Integer.toString(numberOfIterations), 4);
                this.add(textNumberOfIteration);
            }
        });
        this.add(new ButtonForPanel(350, "Mean shift") {
            @Override
            public void buttonAction() {
                applySegmentation("mean_shift");
            }
        });
    }

    private boolean readingColorDistance() {
        if (GUI.isDouble(textColorDistance.getText())) {
            double newValue = Double.parseDouble(textColorDistance.getText());
            if (newValue > 0) {
                colorDistance = newValue;
                return true;
            }
        }
        if (GUI.isInteger(textColorDistance.getText())) {
            int newSigma = Integer.parseInt(textColorDistance.getText());
            if (newSigma > 0) {
                colorDistance = (double) newSigma;
                return true;
            }
        }
        GUI.message.setText("Input correct color distance.");
        return false;
    }

    private boolean numberOfIterations() {
        if (GUI.isInteger(textNumberOfIteration.getText())) {
            int newValue = Integer.parseInt(textNumberOfIteration.getText());
            if (newValue > 0) {
                numberOfIterations = newValue;
                return true;
            }
        }
        GUI.message.setText("Input correct number of iterations.");
        return false;
    }

    private void applySegmentation(String name) {
        if (GUI.isImageSelected == false) {
            GUI.message.setText("Select image.");
            return;
        }

        if (GUI.lastChange != name)
            GUI.overwritingSavedChange();

        if (!readingColorDistance())
            return;

        try {
            GUI.image.setImage(GUI.pathSavedChange);
            GUI.bufferedImage = ImageIO.read(new File(GUI.pathSavedChange));

            if (name == "split_merge") {
                new SplitAndMerge().applySplitAndMerge();
            }

            if (name == "normalized_cut") {
                NormalizedCut normalizedCut = new NormalizedCut();
            }

            if (name == "mean_shift") {
                if (!numberOfIterations())
                    return;
                new MeanShift().applyMeanShift();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
