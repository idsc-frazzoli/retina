// code by mg
package ch.ethz.idsc.demo.mg.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

// sets up the window for all the pipelineFrames
public class PipelineVisualization {
  private final JFrame jFrame = new JFrame();
  private final BufferedImage[] bufferedImage = new BufferedImage[3];
  private final float scaling = 1.5f; // original images are tiny
  private final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      graphics.drawString("Raw event stream", 50, 13);
      graphics.drawImage(HandLabeler.scaleImage(bufferedImage[0], scaling), 50, 20, null);
      graphics.drawString("Filtered event stream with active blobs", 50, 313);
      graphics.drawImage(HandLabeler.scaleImage(bufferedImage[1], scaling), 50, 320, null);
      graphics.drawString("Filtered event stream with hidden blobs", 50, 613);
      graphics.drawImage(HandLabeler.scaleImage(bufferedImage[2], scaling), 50, 620, null);
    }
  };
  private int imageCount = 0;

  public PipelineVisualization() {
    bufferedImage[0] = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
    bufferedImage[1] = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
    bufferedImage[2] = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
    // ---
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setContentPane(jComponent);
    jFrame.setBounds(100, 100, 450, 950);
    jFrame.setVisible(true);
  }

  public void setImage(BufferedImage bufferedImage, int imgNumber) {
    this.bufferedImage[imgNumber] = bufferedImage;
    jComponent.repaint();
  }

  public void saveImage(File pathToFile, String imagePrefix, int timeStamp) throws IOException {
    imageCount++;
    BufferedImage wholeGUI = new BufferedImage(jFrame.getContentPane().getWidth(), jFrame.getContentPane().getHeight(), BufferedImage.TYPE_INT_RGB);
    jFrame.paint(wholeGUI.getGraphics());
    String fileName = String.format("%s_%04d_%d.png", imagePrefix, imageCount, timeStamp);
    ImageIO.write(bufferedImage[1], "png", new File(pathToFile, fileName));
    // ImageIO.write(wholeGUI, "png", new File(HandLabelFileLocations.GUIVisualization+fileName);
    System.out.printf("Images saved as %s\n", fileName);
  }
}
