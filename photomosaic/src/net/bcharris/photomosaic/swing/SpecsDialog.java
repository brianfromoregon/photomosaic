/*
 * SpecsDialog.java
 *
 * Created on January 27, 2008, 9:22 AM
 */
package net.bcharris.photomosaic.swing;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author  brian
 */
public class SpecsDialog extends javax.swing.JDialog
{
	public File sourceImgDir,  outputDir;

	public int maxSameImageUsage,  drillDown,  xDenom,  yDenom;
	
	public boolean cancelled = true;

	private final int numWide, numTall;
	
	/** Creates new form SpecsDialog */
	public SpecsDialog(int numWide, int numTall)
	{
		initComponents();
		pack();
		
		this.numWide = numWide;
		this.numTall = numTall;
		
		for (int i = numWide; i >= 1; i--)
		{
			if (numWide % i == 0)
			{
				xSplitsCombo.addItem(numWide / i);
			}
		}
		
		for (int i = numTall; i >= 1; i--)
		{
			if (numTall % i == 0)
			{
				ySplitsCombo.addItem(numTall / i);
			}
		}
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        setSourceImageDirButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        setOutputDirButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        maxSameImageUsageSlider = new javax.swing.JSpinner();
        jPanel7 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        drillDownSlider = new javax.swing.JSpinner();
        jPanel8 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        xSplitsCombo = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        ySplitsCombo = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        doneButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setFocusable(false);
        setLayout(new java.awt.BorderLayout());

        jPanel5.setFocusable(false);
        jPanel5.setLayout(new java.awt.GridLayout(0, 1, 0, 10));

        jLabel1.setFont(new java.awt.Font("Lucida Sans", 0, 18));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Enter some specs for your mosaic.");
        jLabel1.setAlignmentX(0.5F);
        jLabel1.setFocusable(false);
        jPanel5.add(jLabel1);

        jLabel2.setFont(new java.awt.Font("Lucida Sans", 0, 14));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Hover over something for more detail.");
        jLabel2.setAlignmentX(0.5F);
        jLabel2.setFocusable(false);
        jPanel5.add(jLabel2);

        add(jPanel5, java.awt.BorderLayout.NORTH);

        jPanel4.setFocusable(false);
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setFocusable(false);

        setSourceImageDirButton.setText("Set Source Image Directory");
        setSourceImageDirButton.setToolTipText("Where all your source images live.");
        setSourceImageDirButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setSourceImageDirButtonActionPerformed(evt);
            }
        });
        jPanel1.add(setSourceImageDirButton);

        jPanel4.add(jPanel1);

        jPanel2.setFocusable(false);

        setOutputDirButton.setText("Set Processing Directory");
        setOutputDirButton.setToolTipText("A processing directory for storing intermediate images.  It will be \"rm -rf\"'d so be careful.");
        setOutputDirButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setOutputDirButtonActionPerformed(evt);
            }
        });
        jPanel2.add(setOutputDirButton);

        jPanel4.add(jPanel2);

        jPanel6.setFocusable(false);

        jLabel4.setText("Max Usage of Same Source Image");
        jLabel4.setToolTipText("How many times the same source image can be used in the final mosaic.");
        jLabel4.setFocusable(false);
        jPanel6.add(jLabel4);

        maxSameImageUsageSlider.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(0), null, Integer.valueOf(1)));
        maxSameImageUsageSlider.setToolTipText("How many times the same source image can be used in the final mosaic.");
        maxSameImageUsageSlider.setPreferredSize(new java.awt.Dimension(50, 22));
        jPanel6.add(maxSameImageUsageSlider);

        jPanel4.add(jPanel6);

        jPanel7.setFocusable(false);

        jLabel5.setText("Drill Down Amount");
        jLabel5.setToolTipText("Higher numbers mean better quality mosaics and slower processing, with diminishing returns.  If you're not sure then leave it alone.");
        jLabel5.setFocusable(false);
        jPanel7.add(jLabel5);

        drillDownSlider.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(5), Integer.valueOf(1), null, Integer.valueOf(1)));
        drillDownSlider.setToolTipText("Higher numbers mean better quality mosaics and slower processing, with diminishing returns.  If you're not sure then leave it alone.");
        drillDownSlider.setPreferredSize(new java.awt.Dimension(40, 22));
        jPanel7.add(drillDownSlider);

        jPanel4.add(jPanel7);

        jPanel8.setFocusable(false);

        jLabel6.setText("X Splits");
        jLabel6.setToolTipText("How many intermediate sections to split the final mosiac into, in each direction, to overcome long shell script argument lists.  1 means no splits, 2 means split in half, etc.");
        jLabel6.setFocusable(false);
        jPanel8.add(jLabel6);

        xSplitsCombo.setToolTipText("How many intermediate sections to split the final mosiac into, in each direction, to overcome long shell script argument lists.  1 means no splits, 2 means split in half, etc.");
        xSplitsCombo.setPreferredSize(new java.awt.Dimension(45, 22));
        jPanel8.add(xSplitsCombo);

        jLabel7.setText("Y Splits");
        jLabel7.setToolTipText("How many intermediate sections to split the final mosiac into, in each direction, to overcome long shell script argument lists.  1 means no splits, 2 means split in half, etc.");
        jLabel7.setFocusable(false);
        jPanel8.add(jLabel7);

        ySplitsCombo.setToolTipText("How many intermediate sections to split the final mosiac into, in each direction, to overcome long shell script argument lists.  1 means no splits, 2 means split in half, etc.");
        ySplitsCombo.setPreferredSize(new java.awt.Dimension(45, 22));
        jPanel8.add(ySplitsCombo);

        jPanel4.add(jPanel8);

        add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel3.setFocusable(false);

        doneButton.setText("Done");
        doneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneButtonActionPerformed(evt);
            }
        });
        jPanel3.add(doneButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel3.add(cancelButton);

        add(jPanel3, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents
	private void setSourceImageDirButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setSourceImageDirButtonActionPerformed
		final JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(false);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = fc.showOpenDialog(this);

		if (returnVal != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		this.sourceImgDir = fc.getSelectedFile();
		setSourceImageDirButton.setText(sourceImgDir.getAbsolutePath());
}//GEN-LAST:event_setSourceImageDirButtonActionPerformed

	private void setOutputDirButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setOutputDirButtonActionPerformed
		final JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(false);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = fc.showOpenDialog(this);

		if (returnVal != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		this.outputDir = fc.getSelectedFile();
		setOutputDirButton.setText(outputDir.getAbsolutePath());
}//GEN-LAST:event_setOutputDirButtonActionPerformed

	private void doneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doneButtonActionPerformed
		if (sourceImgDir == null || outputDir == null)
		{
			JOptionPane.showMessageDialog(this, "Set both the source image and output directories.");
			return;
		}
		
		this.maxSameImageUsage = (Integer)maxSameImageUsageSlider.getValue();
		this.drillDown = (Integer)drillDownSlider.getValue();
		this.xDenom = numWide / (Integer)xSplitsCombo.getSelectedItem();
		this.yDenom = numTall / (Integer)ySplitsCombo.getSelectedItem();
		cancelled = false;
		setVisible(false);
}//GEN-LAST:event_doneButtonActionPerformed

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
		cancelled = true;
		setVisible(false);
}//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton doneButton;
    private javax.swing.JSpinner drillDownSlider;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JSpinner maxSameImageUsageSlider;
    private javax.swing.JButton setOutputDirButton;
    private javax.swing.JButton setSourceImageDirButton;
    private javax.swing.JComboBox xSplitsCombo;
    private javax.swing.JComboBox ySplitsCombo;
    // End of variables declaration//GEN-END:variables
}
