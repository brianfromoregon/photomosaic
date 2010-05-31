package net.bcharris.photomosaic;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class VisualCreator extends javax.swing.JFrame {

    private final ProcessedIndex processedIndex;
    private final BufferedImage targetImage;
    private final MosaicPreviewPanel mosaicPreviewPanel;
    private MatchingIndex matchingIndex;

    public VisualCreator(ProcessedIndex processedIndex, BufferedImage targetImage) {
        super("Mosaic Preview");
        this.processedIndex = processedIndex;
        this.targetImage = targetImage;
        initComponents();
        this.mosaicPreviewPanel = new MosaicPreviewPanel();
        getContentPane().add(mosaicPreviewPanel, BorderLayout.CENTER);
        this.matchingIndex = createPreviewMatchingIndex();
        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                edt_preview();
            }
        });
    }

    private MatchingIndex createPreviewMatchingIndex() {
        return MatchingIndex.create(processedIndex, (ColorSpace) colorSpaceComboBox.getSelectedItem(), MatchingIndex.Accuracy.FASTEST);
    }

    private void edt_preview() {
        matchingIndex.resetUsage();
        Mosaic mosaic = new Creator().designMosaic(matchingIndex, targetImage, allowReuseCheckbox.isSelected(), (Integer) densitySpinner.getValue());
        Log.log("Rendering.");
        mosaicPreviewPanel.update(mosaic);
    }

    private void edt_create() {
        MatchingIndex matchingIndex = MatchingIndex.create(processedIndex, (ColorSpace) colorSpaceComboBox.getSelectedItem(), Creator.DEFAULT_ACCURACY);
        Creator creator = new Creator();
        Mosaic mosaic = creator.designMosaic(matchingIndex, targetImage, allowReuseCheckbox.isSelected(), (Integer) densitySpinner.getValue());
        File file = creator.writeToFile(mosaic);
        JOptionPane.showMessageDialog(this, new JTextArea(file.getAbsolutePath()));
    }

    private ComboBoxModel colorSpaceComboBoxModel() {
        return new DefaultComboBoxModel(new Object[]{ColorSpace.CIELAB, ColorSpace.SRGB});
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        colorSpaceComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        densitySpinner = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        allowReuseCheckbox = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        createButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(278, 300));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Knobs"));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        colorSpaceComboBox.setModel(colorSpaceComboBoxModel());
        colorSpaceComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorSpaceComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        jPanel2.add(colorSpaceComboBox, gridBagConstraints);

        jLabel2.setText("Density");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        jPanel2.add(jLabel2, gridBagConstraints);

        densitySpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), null, null, Integer.valueOf(1)));
        densitySpinner.setPreferredSize(new java.awt.Dimension(40, 20));
        densitySpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                densitySpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        jPanel2.add(densitySpinner, gridBagConstraints);

        jLabel1.setText("ColorSpace");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        jPanel2.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(jPanel2, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        allowReuseCheckbox.setText("Allow Reuse");
        allowReuseCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allowReuseCheckboxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        jPanel3.add(allowReuseCheckbox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel1.add(jPanel3, gridBagConstraints);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        createButton.setText("Write To File");
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel4.add(createButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
        jPanel1.add(jPanel4, gridBagConstraints);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        edt_create();
    }//GEN-LAST:event_createButtonActionPerformed

    private void densitySpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_densitySpinnerStateChanged
        edt_preview();
    }//GEN-LAST:event_densitySpinnerStateChanged

    private void allowReuseCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allowReuseCheckboxActionPerformed
        edt_preview();
    }//GEN-LAST:event_allowReuseCheckboxActionPerformed

    private void colorSpaceComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorSpaceComboBoxActionPerformed
        matchingIndex = createPreviewMatchingIndex();
        edt_preview();
    }//GEN-LAST:event_colorSpaceComboBoxActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox allowReuseCheckbox;
    private javax.swing.JComboBox colorSpaceComboBox;
    private javax.swing.JButton createButton;
    private javax.swing.JSpinner densitySpinner;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
        JFileChooser fc = new JFileChooser();
        File indexFile = promptFile(fc, "Select an index file.");
        if (indexFile == null) {
            System.exit(1);
        }
        File targetImageFile = promptFile(fc, "Select a target image file.");
        if (targetImageFile == null) {
            System.exit(1);
        }
        final BufferedImage targetImage = Util.readImage(targetImageFile);
        Log.log("Loading index: %s", indexFile.getAbsolutePath());
        Index index = Util.readIndex(indexFile);
        Log.log("Processing index of size %d", index.images.size());
        final ProcessedIndex processedIndex = ProcessedIndex.process(index, Creator.DEFAULT_DRILL_DOWN);
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new VisualCreator(processedIndex, targetImage).setVisible(true);
            }
        });
    }

    private static File promptFile(JFileChooser fc, String message) {
        JOptionPane.showMessageDialog(null, message);
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        } else {
            return null;
        }
    }
}
