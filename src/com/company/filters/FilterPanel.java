package com.company.filters;

import com.company.*;
import com.company.panel_elements.ButtonForPanel;
import com.company.panel_elements.ParametrFieldForPanel;

import javax.swing.*;
import java.util.Objects;

public class FilterPanel extends JPanel {
    public FilterPanel() {
        super();
        this.setBackground(GUI.cuteColor);
        this.setLayout(null);
        this.setBounds(GUI.dimensionsForChangePanel);

        this.add(new ParametrFieldForPanel(50, "sigma = ") {
            @Override
            public void inputParametrValue() {
                textSigma = new JTextField(Integer.toString(GaussFilter.getSigma()), 10);
                this.add(textSigma);
            }
        });
        this.add(new ButtonForPanel(100, "Gaussian filter") {
            @Override
            public void buttonAction() {
                applyGaussFilter();
            }
        });

        this.add(new ButtonForPanel(200, "Sobel filter") {
            @Override
            public void buttonAction() {
                applySobelFilter();
            }
        });

        this.add(new ParametrFieldForPanel(300, "theta = ") {
            @Override
            public void inputParametrValue() {
                String comboBoxItems[] = { "0", "45", "90", "135"};
                textTheta = new JComboBox(comboBoxItems);
                this.add(textTheta);
            }
        });
        this.add(new ButtonForPanel(350, "Gabor filter") {
            @Override
            public void buttonAction() {
                applyGaborFilter();
            }
        });

    }

    private void applyGaussFilter() {
        GaussFilter gaussFilter = new GaussFilter() {
            @Override
            public boolean readingParameters() {
                return readingSigma(textSigma.getText());
            }
        };
        gaussFilter.applyKernal();
    }

    private void applySobelFilter() {
        SobelFilter sobelFilter = new SobelFilter() {
            @Override
            protected boolean readingParameters() {
                return true;
            }
        };

        sobelFilter.applyKernal();
    }

    private void applyGaborFilter() {
        GaborFilter gaborFilter = new GaborFilter() {
            @Override
            protected boolean readingParameters() {
                return GaborFilter.readingTheta(Objects.requireNonNull(textTheta.getSelectedItem()).toString());
            }
        };

        gaborFilter.applyKernal();
    }

    public static JTextField textSigma;
    public static JComboBox textTheta;
}
