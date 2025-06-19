/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.fhfelipefh.projetofinal_lpoo_felipe;

import com.fhfelipefh.projetofinal_lpoo_felipe.view.MainForm;

import javax.swing.*;

/**
 *
 * @author 20201PF.CC0181
 */
public class ProjetoFinal_LPOO_FELIPE {

    public static void main(String[] args) {
        System.out.println("Projeto Final LPOO - Felipe");
        SwingUtilities.invokeLater(() -> {
            MainForm form = new MainForm();
            JFrame frame = new JFrame("Reservas de Salas");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(form.getRootComponent());
            frame.pack();
            frame.setSize(1024, 768);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
