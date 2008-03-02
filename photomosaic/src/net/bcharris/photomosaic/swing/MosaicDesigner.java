/*
 * MosaicDesigner.java
 *
 * Created on January 25, 2008, 6:12 PM
 */
package net.bcharris.photomosaic.swing;

import java.awt.Dimension;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import net.bcharris.photomosaic.builder.ImageFileContext;
import net.bcharris.photomosaic.builder.ImagePalette;
import net.bcharris.photomosaic.util.ImageMagickUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  brian
 */
public class MosaicDesigner extends javax.swing.JFrame
{
	private static final transient Log log = LogFactory.getLog(MosaicDesigner.class);

	private int numSourceImagesTall,  numSourceImagesWide;

	private int sourceImageWidth,  sourceImageHeight;

	/** Creates new form MosaicDesigner */
	public MosaicDesigner()
	{
		super("Mosaic Designer");
		// Auto-generate GUI init
		initComponents();

		// Grow to a decent size
		setSize(650, 600);

		// Center on the screen.
		setLocationRelativeTo(null);
	}

	public static void main(String args[])
	{
		java.awt.EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				new MosaicDesigner().setVisible(true);
			}
		});
	}

	private void resetTargetImage()
	{
		final JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(false);

		int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File f = fc.getSelectedFile();

			final BufferedImage newTargetImg;
			try
			{
				newTargetImg = ImageIO.read(f);
				if (newTargetImg == null)
				{
					throw new IllegalStateException(String.format("User-selected image file is a non-image '%s'", f.getAbsolutePath()));
				}

				targetImageGridPanel.setImage(newTargetImg);
			}
			catch (Throwable t)
			{
				log.warn("When loading user-selected image", t);
				return;
			}
		}
	}

	private void recalcAndUpdateUI()
	{
		if (targetImageGridPanel.getImage() == null)
		{
			return;
		}

		try
		{
			finalMosaicWidthTextField.commitEdit();
			numSourceImagesTallSlider.commitEdit();
			sourceImageWidthSpinner.commitEdit();
		}
		catch (Throwable t)
		{
			log.warn("Invalid input values, aborting.");
			return;
		}

		// Start with what we know
		int desiredFinalMosaicWidth = Integer.parseInt(finalMosaicWidthTextField.getValue().toString());
		numSourceImagesTall = Integer.parseInt(numSourceImagesTallSlider.getValue().toString());
		int desiredSourceImageWidth = Integer.parseInt(sourceImageWidthSpinner.getValue().toString());

		// Calculate the rest
		double desiredFinalMosaicHeight = (desiredFinalMosaicWidth * (1 / targetImageGridPanel.getImageRatio()));
		sourceImageHeight = (int) Math.round(desiredFinalMosaicHeight / numSourceImagesTall);
		int finalMosaicHeight = sourceImageHeight * numSourceImagesTall;
		numSourceImagesWide = Math.round(desiredFinalMosaicWidth / (float) desiredSourceImageWidth);
		sourceImageWidth = Math.round(desiredFinalMosaicWidth / (float) numSourceImagesWide);
		int finalMosaicWidth = sourceImageWidth * numSourceImagesWide;
		int requiredSourceImages = numSourceImagesTall * numSourceImagesWide;
		double sourceImageRatio = (finalMosaicWidth / (double) numSourceImagesWide) / (finalMosaicHeight / (double) numSourceImagesTall);

		requiredSourceImagesLabel.setText(stripValue(requiredSourceImagesLabel.getText()) + requiredSourceImages);
		finalMosaicSizeLabel.setText(stripValue(finalMosaicSizeLabel.getText()) + finalMosaicWidth + "x" + finalMosaicHeight);
		sourceImageRatioLabel.setText(stripValue(sourceImageRatioLabel.getText()) + sourceImageRatio);
		sourceImageSizeLabel.setText(stripValue(sourceImageSizeLabel.getText()) + sourceImageWidth + "x" + sourceImageHeight);

		// Update input constraints
		((SpinnerNumberModel) sourceImageWidthSpinner.getModel()).setMaximum(finalMosaicWidth);

		// Redraw target image grid.
		targetImageGridPanel.setGridSize(numSourceImagesWide, numSourceImagesTall);

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				targetImageGridPanel.repaint();
			}
		});
	}

	private static String stripValue(Object o)
	{
		int index = o.toString().indexOf("= ");
		if (index == -1)
		{
			return o.toString();
		}
		return o.toString().substring(0, index + 2);
	}

	private void createMosaic()
	{
		if (targetImageGridPanel.getImage() == null)
		{
			JOptionPane.showMessageDialog(this, "Choose a target image first.");
			return;
		}

		ImageGridPanel targetImageGridPanelClone = new ImageGridPanel(targetImageGridPanel);
		SetPrioritiesDialog setPrioritiesDialog = new SetPrioritiesDialog(this, targetImageGridPanelClone);
		setPrioritiesDialog.setModal(true);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setPrioritiesDialog.setSize((int) (d.getWidth() * .9), (int) (d.getHeight() * .85));
		setPrioritiesDialog.setLocationRelativeTo(null);
		setPrioritiesDialog.setVisible(true);

		// And we're back

		if (setPrioritiesDialog.cancelled)
		{
			return;
		}

		targetImageGridPanel.setPriorities(targetImageGridPanelClone.getPriorities());

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				targetImageGridPanel.repaint();
			}
		});

		SpecsDialog specs = new SpecsDialog(numSourceImagesWide, numSourceImagesTall);
		specs.setModal(true);
		specs.setLocationRelativeTo(null);
		specs.setVisible(true);

		// And we're back again

		if (specs.cancelled)
		{
			return;
		}

		File srcDir = specs.sourceImgDir;
		File outDir = specs.outputDir;
		int preferredMaxSameImageUsage = specs.preferredMaxSameImageUsage;

		JOptionPane.showMessageDialog(this, new Object[]{
			"Run these commands to generate your palette, then click OK to let me process the results (which can take a while).",
			new TextArea(ImageMagickUtil.generateScriptToPrepSourceImages(srcDir, outDir.getAbsolutePath(), sourceImageWidth, sourceImageHeight))
		});

		ImagePalette imagePalette;
		setEnabled(false);
		try
		{
			imagePalette = new ImagePalette(specs.drillDown, 8);
			imagePalette.addImages(outDir);
		}
		finally
		{
			setEnabled(true);
		}
		int needed = numSourceImagesWide * numSourceImagesTall;
		int maxSameImageUsage = preferredMaxSameImageUsage;
		if (needed > imagePalette.size() * preferredMaxSameImageUsage)
		{
			maxSameImageUsage = (int) Math.ceil(needed / (double) imagePalette.size());
			JOptionPane.showMessageDialog(this, "Not enough source images to create requested mosaic; increasing the max number of times a source image can be used to " + maxSameImageUsage);
		}

                // Useless dialog
//		JOptionPane.showMessageDialog(this, "This next step may take a while.  Another dialog will popup when it's finished.");

		File[][] imageGrid;
		setEnabled(false);
		try
		{
			// A grid of images that, when compacted into 1 large image w.r.t. their
			// ordering in the grid, will compose the desired mosaic.
			imageGrid = toFiles(imagePalette.bestMatches(targetImageGridPanel.getImage(), numSourceImagesWide, numSourceImagesTall, maxSameImageUsage, targetImageGridPanel.getPriorities()));
		}
		finally
		{
			setEnabled(true);
		}
		String createScript = ImageMagickUtil.generateScriptToCreateMosaic("montage", imageGrid, 1, numSourceImagesTall, outDir.getAbsolutePath());

		JOptionPane.showMessageDialog(this, new Object[]{
			"This is it, run these and you're done.",
			new TextArea(createScript)
		});
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jPanel3 = new javax.swing.JPanel();
        reqPanel = new javax.swing.JPanel();
        requiredSourceImagesLabel = new javax.swing.JLabel();
        finalMosaicSizeLabel = new javax.swing.JLabel();
        sourceImageSizeLabel = new javax.swing.JLabel();
        sourceImageRatioLabel = new javax.swing.JLabel();
        targetImageGridPanel = new net.bcharris.photomosaic.swing.ImageGridPanel();
        jPanel1 = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        setTargetImageButton = new javax.swing.JButton();
        createMosaicButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        controlPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        finalMosaicWidthTextField = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        numSourceImagesTallSlider = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        sourceImageWidthSpinner = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setFocusable(false);

        reqPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Status"));
        reqPanel.setFocusable(false);
        reqPanel.setLayout(new java.awt.GridLayout(0, 2, 15, 10));

        requiredSourceImagesLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        requiredSourceImagesLabel.setText("Number of Cells = ?");
        requiredSourceImagesLabel.setFocusable(false);
        reqPanel.add(requiredSourceImagesLabel);

        finalMosaicSizeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        finalMosaicSizeLabel.setText("Final Mosaic Size = ?");
        finalMosaicSizeLabel.setFocusable(false);
        reqPanel.add(finalMosaicSizeLabel);

        sourceImageSizeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sourceImageSizeLabel.setText("Source Image Size = ?");
        sourceImageSizeLabel.setFocusable(false);
        reqPanel.add(sourceImageSizeLabel);

        sourceImageRatioLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sourceImageRatioLabel.setText("Source Image W/H Ratio = ?");
        sourceImageRatioLabel.setFocusable(false);
        reqPanel.add(sourceImageRatioLabel);

        jPanel3.add(reqPanel);

        getContentPane().add(jPanel3, java.awt.BorderLayout.NORTH);

        targetImageGridPanel.setFocusable(false);
        getContentPane().add(targetImageGridPanel, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        buttonPanel.setFocusable(false);

        setTargetImageButton.setText("Set Target Image");
        setTargetImageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setTargetImageButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(setTargetImageButton);

        createMosaicButton.setText("Create This Mosaic!");
        createMosaicButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createMosaicButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(createMosaicButton);

        jPanel1.add(buttonPanel);

        jPanel2.setFocusable(false);

        controlPanel.setFocusable(false);
        controlPanel.setLayout(new java.awt.GridLayout(0, 2, 10, 0));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Approx. Final Mosaic Width (px):");
        jLabel5.setAlignmentX(0.5F);
        jLabel5.setFocusable(false);
        controlPanel.add(jLabel5);
        jLabel5.getAccessibleContext().setAccessibleName("Image Size");

        finalMosaicWidthTextField.setColumns(4);
        finalMosaicWidthTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        finalMosaicWidthTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        finalMosaicWidthTextField.setText("1600");
        finalMosaicWidthTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                finalMosaicWidthTextFieldFocusLost(evt);
            }
        });
        controlPanel.add(finalMosaicWidthTextField);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Number of Source Images Tall:");
        jLabel2.setToolTipText("Width to height ratio used to crop source images.");
        jLabel2.setAlignmentX(0.5F);
        jLabel2.setFocusable(false);
        controlPanel.add(jLabel2);

        numSourceImagesTallSlider.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(16), Integer.valueOf(1), null, Integer.valueOf(1)));
        numSourceImagesTallSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                numSourceImagesTallSliderStateChanged(evt);
            }
        });
        controlPanel.add(numSourceImagesTallSlider);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Approx. Source Image Width (px):");
        jLabel6.setAlignmentX(0.5F);
        jLabel6.setFocusable(false);
        controlPanel.add(jLabel6);

        sourceImageWidthSpinner.setModel(new javax.swing.SpinnerNumberModel(100, 1, 1600, 1));
        sourceImageWidthSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sourceImageWidthSpinnerStateChanged(evt);
            }
        });
        controlPanel.add(sourceImageWidthSpinner);

        jPanel2.add(controlPanel);

        jPanel1.add(jPanel2);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents
	private void setTargetImageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setTargetImageButtonActionPerformed
		resetTargetImage();
		recalcAndUpdateUI();
}//GEN-LAST:event_setTargetImageButtonActionPerformed

	private void finalMosaicWidthTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_finalMosaicWidthTextFieldFocusLost
		recalcAndUpdateUI();
	}//GEN-LAST:event_finalMosaicWidthTextFieldFocusLost

	private void numSourceImagesTallSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_numSourceImagesTallSliderStateChanged
		recalcAndUpdateUI();
	}//GEN-LAST:event_numSourceImagesTallSliderStateChanged

	private void sourceImageWidthSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sourceImageWidthSpinnerStateChanged
		recalcAndUpdateUI();
	}//GEN-LAST:event_sourceImageWidthSpinnerStateChanged

	private void createMosaicButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createMosaicButtonActionPerformed
		createMosaic();
}//GEN-LAST:event_createMosaicButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JButton createMosaicButton;
    private javax.swing.JLabel finalMosaicSizeLabel;
    private javax.swing.JFormattedTextField finalMosaicWidthTextField;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSpinner numSourceImagesTallSlider;
    private javax.swing.JPanel reqPanel;
    private javax.swing.JLabel requiredSourceImagesLabel;
    private javax.swing.JButton setTargetImageButton;
    private javax.swing.JLabel sourceImageRatioLabel;
    private javax.swing.JLabel sourceImageSizeLabel;
    private javax.swing.JSpinner sourceImageWidthSpinner;
    private net.bcharris.photomosaic.swing.ImageGridPanel targetImageGridPanel;
    // End of variables declaration//GEN-END:variables
	private static File[][] toFiles(ImageFileContext[][] contexts)
	{
		File[][] files = new File[contexts.length][];

		for (int i = 0; i < contexts.length; i++)
		{
			files[i] = new File[contexts[i].length];
			for (int j = 0; j < contexts[i].length; j++)
			{
				files[i][j] = contexts[i][j].file;
			}
		}

		return files;
	}
}
