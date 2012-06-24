package com.brianfromoregon.photomosaic.indexes;

import javax.swing.*;

public class PaletteViewer extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    public PaletteViewer() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
    }
}
