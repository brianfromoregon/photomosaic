/*
 * SetPrioritiesDialog.java
 *
 * Created on January 26, 2008, 1:08 PM
 */
package net.bcharris.photomosaic.swing;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

/**
 *
 * @author  brian
 */
public class SetPrioritiesDialog extends javax.swing.JDialog
{
	private final ImageGridPanel imageGridPanel;
	private final MouseAdapter mouseHandler;
	
	public boolean cancelled = true;
	
	/** Creates new form SetPrioritiesDialog */
	public SetPrioritiesDialog(Frame owner, ImageGridPanel imageGridPanel)
	{
		super(owner, "Set Section Priorities");
		
		this.imageGridPanel = imageGridPanel;
		initComponents();
		this.mouseHandler = new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				changePriority(e.getPoint());
			}
			
			@Override
			public void mouseDragged(MouseEvent e)
			{
				changePriority(e.getPoint());
			}
		};
		this.imageGridPanel.addMouseListener(mouseHandler);
		this.imageGridPanel.addMouseMotionListener(mouseHandler);
		add(imageGridPanel, BorderLayout.CENTER);
	}
	
	// Not necessary
//	@Override
//	protected void processWindowEvent(WindowEvent e)
//	{
//		super.processWindowEvent(e);
//		if (e.getID() == WindowEvent.WINDOW_CLOSING)
//		{
//			// Clean up when we're done.
//			removeMouseListener(mouseHandler);
//			removeMouseMotionListener(mouseHandler);
//		}
//	}
	
	private void changePriority(Point p)
	{
		Point cell = imageGridPanel.getCellForPoint(p);
		
		// Outside of image
		if (cell == null)
		{
			return;
		}
		
		int radius = mouseInfluenceRadiusSlider.getValue();
		
		if (leftToRightOption.isSelected())
		{
			for (int i = cell.x - radius; i <= cell.x + radius; i++)
			{
				for (int j = cell.y - radius; j <= cell.y + radius; j++)
				{
					setPriority(new Point(i, j));
				}
			}
		}
		else if (rightToLeftOption.isSelected())
		{
			for (int i = cell.x + radius; i >= cell.x - radius; i--)
			{
				for (int j = cell.y - radius; j <= cell.y + radius; j++)
				{
					setPriority(new Point(i, j));
				}
			}
		}
		else if (topToBottomOption.isSelected())
		{
			for (int j = cell.y - radius; j <= cell.y + radius; j++)
			{
				for (int i = cell.x - radius; i <= cell.x + radius; i++)
				{
					setPriority(new Point(i, j));
				}
			}
		}
		else if (bottomToTopOption.isSelected())
		{
			for (int j = cell.y + radius; j >= cell.y - radius; j--)
			{
				for (int i = cell.x - radius; i <= cell.x + radius; i++)
				{
					setPriority(new Point(i, j));
				}
			}
		}
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				imageGridPanel.repaint();
			}
		});
	}
	
	private void setPriority(Point cell)
	{
		imageGridPanel.removePriority(cell);
		if (addToFrontButton.isSelected())
		{
			imageGridPanel.addPriority(0, cell);
		}
		else if (addToEndButton.isSelected())
		{
			imageGridPanel.addPriority(cell);
		}
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        doneButton = new javax.swing.JButton();
        startOverButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        mouseInfluenceRadiusSlider = new javax.swing.JSlider();
        jPanel2 = new javax.swing.JPanel();
        addToFrontButton = new javax.swing.JRadioButton();
        addToEndButton = new javax.swing.JRadioButton();
        jPanel7 = new javax.swing.JPanel();
        leftToRightOption = new javax.swing.JRadioButton();
        rightToLeftOption = new javax.swing.JRadioButton();
        topToBottomOption = new javax.swing.JRadioButton();
        bottomToTopOption = new javax.swing.JRadioButton();

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        jLabel1.setFont(new java.awt.Font("Lucida Sans", 0, 18));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Choose the priorities of the mosaic sections (blank is ok too.)");
        jLabel1.setAlignmentX(0.5F);
        jPanel3.add(jLabel1);

        jLabel2.setFont(new java.awt.Font("Lucida Sans", 0, 14));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Just click, or click and hold then drag.");
        jLabel2.setAlignmentX(0.5F);
        jPanel3.add(jLabel2);

        getContentPane().add(jPanel3, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        doneButton.setText("Done");
        doneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneButtonActionPerformed(evt);
            }
        });
        jPanel4.add(doneButton);

        startOverButton.setText("Start Over");
        startOverButton.setAlignmentX(0.5F);
        startOverButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startOverButtonActionPerformed(evt);
            }
        });
        jPanel4.add(startOverButton);

        jPanel1.add(jPanel4);

        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.X_AXIS));

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Mouse Influence Radius (# cells)"));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.Y_AXIS));

        mouseInfluenceRadiusSlider.setMajorTickSpacing(2);
        mouseInfluenceRadiusSlider.setMaximum(10);
        mouseInfluenceRadiusSlider.setMinorTickSpacing(1);
        mouseInfluenceRadiusSlider.setPaintLabels(true);
        mouseInfluenceRadiusSlider.setPaintTicks(true);
        mouseInfluenceRadiusSlider.setSnapToTicks(true);
        mouseInfluenceRadiusSlider.setValue(0);
        jPanel5.add(mouseInfluenceRadiusSlider);

        jPanel6.add(jPanel5);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Order"));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        buttonGroup1.add(addToFrontButton);
        addToFrontButton.setText("Add to Front");
        jPanel2.add(addToFrontButton);

        buttonGroup1.add(addToEndButton);
        addToEndButton.setSelected(true);
        addToEndButton.setText("Add to End");
        jPanel2.add(addToEndButton);

        jPanel6.add(jPanel2);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Filling"));
        jPanel7.setLayout(new java.awt.GridLayout(2, 2));

        buttonGroup2.add(leftToRightOption);
        leftToRightOption.setSelected(true);
        leftToRightOption.setText("Left to Right");
        jPanel7.add(leftToRightOption);

        buttonGroup2.add(rightToLeftOption);
        rightToLeftOption.setText("Right to Left");
        jPanel7.add(rightToLeftOption);

        buttonGroup2.add(topToBottomOption);
        topToBottomOption.setText("Top to Bottom");
        jPanel7.add(topToBottomOption);

        buttonGroup2.add(bottomToTopOption);
        bottomToTopOption.setText("Bottom to Top");
        jPanel7.add(bottomToTopOption);

        jPanel6.add(jPanel7);

        jPanel1.add(jPanel6);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

	private void startOverButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startOverButtonActionPerformed
		this.imageGridPanel.clearPriorities();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				imageGridPanel.repaint();
			}
		});
	}//GEN-LAST:event_startOverButtonActionPerformed

	private void doneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doneButtonActionPerformed
		cancelled = false;
		setVisible(false);
	}//GEN-LAST:event_doneButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton addToEndButton;
    private javax.swing.JRadioButton addToFrontButton;
    private javax.swing.JRadioButton bottomToTopOption;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton doneButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JRadioButton leftToRightOption;
    private javax.swing.JSlider mouseInfluenceRadiusSlider;
    private javax.swing.JRadioButton rightToLeftOption;
    private javax.swing.JButton startOverButton;
    private javax.swing.JRadioButton topToBottomOption;
    // End of variables declaration//GEN-END:variables
}
