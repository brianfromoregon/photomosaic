package com.brianfromoregon.photomosaic.indexes;

import com.brianfromoregon.photomosaic.Env;
import com.brianfromoregon.photomosaic.Index;
import com.brianfromoregon.photomosaic.Log;
import com.google.common.io.Files;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class PaletteSelector extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton useSampleButton;
    private JButton newButton;
    private JButton loadButton;
    private JTable recentPalettes;

    public Index selected;

    public PaletteSelector() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        useSampleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                SamplePalettes dialog = new SamplePalettes();
                dialog.pack();
                dialog.setVisible(true);
                if (dialog.chosen != null) {
                    try {
                        selected = dialog.chosen.generate(Files.createTempDir());
                    } catch (IOException e1) {
                        Log.log("Problem creating sample images: %s", e1.getMessage());
                    }
                    dispose();
                }
            }
        });
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
        DefaultMutableTreeNode topic = new DefaultMutableTreeNode("What's a palette?");
        root.add(topic);
        topic.add(new DefaultMutableTreeNode("A palette ie A palette ie A palette ie A palette ie A palette ie A palette ie A palette ie "));
    }

    private void onOK() {
// add your code here
        dispose();
    }

    public static void main(String[] args) {
        PaletteSelector selector = new PaletteSelector();
        selector.pack();
        selector.setVisible(true);
        if (selector.selected != null) {
            PaletteViewer viewer = new PaletteViewer(selector.selected);
            viewer.pack();
            viewer.setVisible(true);
        }
        System.exit(0);
    }
}
