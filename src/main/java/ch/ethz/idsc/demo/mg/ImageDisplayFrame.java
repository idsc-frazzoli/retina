// code by jph
package ch.ethz.idsc.demo.mg;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

class ImageDisplayFrame {
  private final JFrame jFrame = new JFrame();
  private BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
  private JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      graphics.drawImage(bufferedImage, 0, 0, null);
      System.out.println("repaint");
    }
  };

  public ImageDisplayFrame() {
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setContentPane(jComponent);
    jFrame.setBounds(100, 100, 400, 400);
    jFrame.setVisible(true);
  }

  public void setImage(BufferedImage bufferedImage) {
    this.bufferedImage = bufferedImage;
    jComponent.repaint();
  }

  public static void main(String[] args) throws InterruptedException {
    ImageDisplayFrame imageDisplayFrame = new ImageDisplayFrame();
    int count = 0;
    while (imageDisplayFrame.jFrame.isVisible()) {
      BufferedImage bufferedImage = ImageSynthAndExportDemo.createImage((count * 10) % 240);
      Graphics2D graphics = bufferedImage.createGraphics();
      graphics.drawString("" + count, 10, 10);
      imageDisplayFrame.setImage(bufferedImage);
      Thread.sleep(500);
      ++count;
    }
  }
}
