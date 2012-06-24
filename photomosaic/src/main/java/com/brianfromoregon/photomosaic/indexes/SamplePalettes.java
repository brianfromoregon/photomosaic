package com.brianfromoregon.photomosaic.indexes;

import com.brianfromoregon.photomosaic.Env;
import com.brianfromoregon.photomosaic.Index;
import com.brianfromoregon.photomosaic.Indexer;
import com.brianfromoregon.photomosaic.Util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SamplePalettes extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton solidColorsRadioButton;
    private JRadioButton grayscaleRadioButton;

    public Sample chosen = null;

    enum Sample {
        SOLID_COLORS {
            @Override
            Index generate(File outputDir) throws IOException {
                for (int r = 0; r < 256; r += 50) {
                    for (int g = 0; g < 256; g += 50) {
                        for (int b = 0; b < 256; b += 50) {
                            BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

                            Graphics2D g2d = image.createGraphics();
                            g2d.setColor(new Color(r, g, b));
                            g2d.fillRect(0, 0, 1, 1);

                            ImageIO.write(image, "JPG", new File(outputDir, String.valueOf(r) + "_" + String.valueOf(g) + "_" + String.valueOf(b) + ".jpg"));
                        }
                    }
                }

                return new Indexer().index(outputDir,  1, 1);
            }
        }, GRAYSCALE {
            @Override
            Index generate(File outputDir) throws IOException {
                for (int i = 0; i < 256; i += 10) {
                    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

                    Graphics2D g2d = image.createGraphics();
                    g2d.setColor(new Color(i, i, i));
                    g2d.fillRect(0, 0, 1, 1);

                    ImageIO.write(image, "JPG", new File(outputDir, String.valueOf(i) + ".jpg"));
                }

                return new Indexer().index(outputDir, 1, 1);
            }
        };

        abstract Index generate(File outputDir) throws IOException;
    }

    public SamplePalettes() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        if (solidColorsRadioButton.isSelected())
            chosen = Sample.SOLID_COLORS;
        else if (grayscaleRadioButton.isSelected())
            chosen = Sample.GRAYSCALE;
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
