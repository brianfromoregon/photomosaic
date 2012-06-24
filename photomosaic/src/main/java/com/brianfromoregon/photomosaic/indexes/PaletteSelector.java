package com.brianfromoregon.photomosaic.indexes;

import com.brianfromoregon.photomosaic.Index;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PaletteSelector extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton useSampleButton;
    private JButton newButton;
    private JButton loadButton;
    private JTable recentPalettes;

    public Index selector;

    public PaletteSelector() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        useSampleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                SamplePalettes dialog = new SamplePalettes();
                dialog.pack();
                dialog.setVisible(true);
                if (dialog.chosen != null) {
                }
            }
        });
    }

    private void onOK() {
// add your code here
        dispose();
    }

    public static void main(String[] args) {
        PaletteSelector dialog = new PaletteSelector();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
