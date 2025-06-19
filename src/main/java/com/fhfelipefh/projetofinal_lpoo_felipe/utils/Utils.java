package com.fhfelipefh.projetofinal_lpoo_felipe.utils;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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
        NumberFormat display = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        DecimalFormat editFmtPattern = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        editFmtPattern.applyPattern("#,##0.00");
        editFmtPattern.setParseBigDecimal(true);

        NumberFormatter displayFmt = new NumberFormatter(display);
        displayFmt.setValueClass(BigDecimal.class);
        displayFmt.setAllowsInvalid(true);

        NumberFormatter editFmt = new NumberFormatter(editFmtPattern);
        editFmt.setValueClass(BigDecimal.class);
        editFmt.setAllowsInvalid(true);
        editFmt.setOverwriteMode(false);
        editFmt.setCommitsOnValidEdit(true);

        DefaultFormatterFactory factory =
                new DefaultFormatterFactory(displayFmt, displayFmt, editFmt, displayFmt);

        JFormattedTextField f = new JFormattedTextField(factory);
        f.setFocusLostBehavior(JFormattedTextField.COMMIT);
        f.setPreferredSize(new Dimension(200, 25));
        f.setHorizontalAlignment(SwingConstants.RIGHT);
        return f;
    }

}
