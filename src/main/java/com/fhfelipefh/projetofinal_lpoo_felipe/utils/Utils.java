package com.fhfelipefh.projetofinal_lpoo_felipe.utils;

import javax.swing.*;
import java.awt.*;

public class Utils {

    public static JButton createColoredButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(fg);
        return b;
    }

}
