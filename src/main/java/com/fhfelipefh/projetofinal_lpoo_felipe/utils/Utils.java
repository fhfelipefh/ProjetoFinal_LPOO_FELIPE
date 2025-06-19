package com.fhfelipefh.projetofinal_lpoo_felipe.utils;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class Utils {

    public static JButton createColoredButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(fg);
        return b;
    }

    public static JFormattedTextField createCurrencyField() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        NumberFormatter formatter = new NumberFormatter(nf);
        formatter.setValueClass(BigDecimal.class);
        formatter.setAllowsInvalid(false);
        JFormattedTextField ftf = new JFormattedTextField(formatter);
        ftf.setPreferredSize(new Dimension(200, 25));
        return ftf;
    }

}
