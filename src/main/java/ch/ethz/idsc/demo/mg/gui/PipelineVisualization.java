// code by mg
package ch.ethz.idsc.demo.mg.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.owl.bot.util.UserHome;

// sets up the window for all the pipelineFrames
public class PipelineVisualization {
  private final JFrame jFrame = new JFrame();
  private final BufferedImage[] bufferedImage = new BufferedImage[3];
  private final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      graphics.drawString("Raw event stream", 50, 13);
      graphics.drawImage(bufferedImage[0], 50, 20, null);
      graphics.drawString("Filtered event stream with active blobs", 50, 213);
      graphics.drawImage(bufferedImage[1], 50, 220, null);
      graphics.drawString("Filtered event stream with hidden blobs", 50, 413);
      graphics.drawImage(bufferedImage[2], 50, 420, null);
    }
  };
  private int imageCount = 0;

  public PipelineVisualization() {
    bufferedImage[0] = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
    bufferedImage[1] = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
    bufferedImage[2] = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
    // ---
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setContentPane(jComponent);
    jFrame.setBounds(100, 100, 350, 650);
    jFrame.setVisible(true);
  }

  public void setImage(BufferedImage bufferedImage, int imgNumber) {
    this.bufferedImage[imgNumber] = bufferedImage;
    jComponent.repaint();
  }

  public void saveImages() throws IOException {
    imageCount++;
    // ImageIO.write(bufferedImage[0], "png", UserHome.Pictures(String.format("example%03d.png", imageCount)));
    ImageIO.write(bufferedImage[1], "png", UserHome.Pictures(String.format("exampleActive%03d.png", imageCount)));
    ImageIO.write(bufferedImage[2], "png", UserHome.Pictures(String.format("exampleHidden%03d.png", imageCount)));
    System.out.printf("Images saved as example%03d.png\n", imageCount);
  }
}
