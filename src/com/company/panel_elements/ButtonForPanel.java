package com.company.panel_elements;

import com.company.GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class ButtonForPanel extends JButton {
    public ButtonForPanel(int y, String name){
        super(name);
        this.setBounds(20, y, GUI.widthChangePanel - 40, GUI.heightButton);
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonAction();
            }
        });
        this.setBackground(GUI.cuteColor);
    }

    public abstract void buttonAction();
}
