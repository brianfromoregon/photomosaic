package com.brianfromoregon.tiles.indexes;

import com.brianfromoregon.tiles.Index;
import com.brianfromoregon.tiles.web.PaletteImageServer;
import org.joda.time.DateTime;

import javax.swing.*;

public class PaletteViewer extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel paletteSizeLabel;
    private JLabel paletteDateLabel;

    private final Index palette;
    private final PaletteImageServer server;
    public PaletteViewer(Index palette) {
        this.palette = palette;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        paletteSizeLabel.setText(String.valueOf(palette.images.size()));
        paletteDateLabel.setText(new DateTime(palette.created).toString());

        server = new PaletteImageServer(8888, palette);
        server.serve();
    }

    @Override
    public void dispose() {
        super.dispose();
        server.stop();
    }
}
