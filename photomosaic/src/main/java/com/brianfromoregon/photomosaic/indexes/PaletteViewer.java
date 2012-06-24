package com.brianfromoregon.photomosaic.indexes;

import com.brianfromoregon.photomosaic.Index;
import org.joda.time.DateTime;

import javax.swing.*;

public class PaletteViewer extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel paletteSizeLabel;
    private JLabel paletteDateLabel;

    private final Index palette;
    public PaletteViewer(Index palette) {
        this.palette = palette;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        paletteSizeLabel.setText(String.valueOf(palette.images.size()));
        paletteDateLabel.setText(new DateTime(palette.created).toString());
    }
}
