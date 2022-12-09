package com.company.panel_elements;

import com.company.GUI;

import javax.swing.*;

public abstract class ParametrFieldForPanel extends JPanel {
    public ParametrFieldForPanel(int y, String name) {
        super();
        this.setBounds(0, y, GUI.widthChangePanel, GUI.heightButton);
        this.setBackground(GUI.cuteColor);
        JLabel label = new JLabel(name);
        this.add(label);
        inputParametrValue();
    }

    public abstract void inputParametrValue();

}
