// code by mg
package ch.ethz.idsc.demo.mg.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

// provides a visualization of the complete pipeline
public class PipelineVisualization {
  private final JFrame jFrame = new JFrame();
  private final BufferedImage[] bufferedImage = new BufferedImage[6];
  private final float scaling = 1.5f; // original images are tiny
  private int imageCount = 0;
  private final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      graphics.drawString("Raw event stream", 50, 13);
      graphics.drawImage(HandLabeler.scaleImage(bufferedImage[0], scaling), 50, 20, null);
      graphics.drawString("Filtered event stream with active blobs", 50, 313);
      graphics.drawImage(HandLabeler.scaleImage(bufferedImage[1], scaling), 50, 320, null);
      graphics.drawString("Filtered event stream with hidden blobs", 50, 613);
      graphics.drawImage(HandLabeler.scaleImage(bufferedImage[2], scaling), 50, 620, null);
      graphics.drawString("Raw features in physical space", 460, 13);
      graphics.drawImage(HandLabeler.scaleImage(bufferedImage[3], scaling), 460, 20, null);
      graphics.drawString("Estimated features in physical space", 460, 313);
      graphics.drawImage(HandLabeler.scaleImage(bufferedImage[4], scaling), 460, 320, null);
      graphics.drawString("Maybe here show the planned trajectory?", 460, 613);
      graphics.drawImage(HandLabeler.scaleImage(bufferedImage[5], scaling), 460, 620, null);
    }
  };

  public PipelineVisualization() {
    bufferedImage[0] = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
    bufferedImage[1] = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
    bufferedImage[2] = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
    bufferedImage[3] = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
    bufferedImage[4] = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
    bufferedImage[5] = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
    // ---
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setContentPane(jComponent);
    jFrame.setBounds(100, 100, 850, 950);
    jFrame.setVisible(true);
  }

  // set all frames and repaint
  public void setFrames(BufferedImage[] bufferedImages) {
    for (int i = 0; i < bufferedImages.length; i++) {
      bufferedImage[i] = bufferedImages[i];
    }
    jComponent.repaint();
  }

  // for visualization
  public BufferedImage getGUIFrame() {
    return new BufferedImage(jFrame.getContentPane().getWidth(), jFrame.getContentPane().getHeight(), BufferedImage.TYPE_INT_RGB);
  }
}
