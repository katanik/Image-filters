package com.company;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.fast.FastLookAndFeel");
            GUI gui = new GUI();
            gui.setVisible(true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}