package ch.ethz.idsc.demo.mg.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ch.ethz.idsc.demo.mg.pipeline.DavisSingleBlob;
import ch.ethz.idsc.owl.bot.util.UserHome;

// visualizes the pipeline computations. long term goal: develop this into something similar to DavisDetailViewerModule
public class PipelineVisualization {
  // for the visualization
  private BufferedImage bufferedImage = new BufferedImage(240, 180, BufferedImage.TYPE_BYTE_GRAY);
  private Graphics2D graphics = bufferedImage.createGraphics();
  private JFrame jFrame = new JFrame();
  private JPanel jPanel = new JPanel();
  // fields
  private int imageCount = 0;

  public PipelineVisualization() {
  }

  public void showImage() throws IOException {
    jFrame.setSize(600, 600);
    jFrame.setVisible(true);
    jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jFrame.setTitle("Testing");
    jPanel = (JPanel) jFrame.getContentPane();
  }

  public void generateImage(List<DavisSingleBlob> activeBlobs) throws IOException {
    if (activeBlobs.size() == 0) {
      System.out.println("********No active blob present");
      return;
    }
    // TODO collect all events in the update interval and visualize them as well
    // TODO generate a window and show the images in a stream
    System.out.println("****generating image");
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, 240, 180);
    graphics.setColor(Color.BLACK);
    AffineTransform old = graphics.getTransform();
    for (int i = 0; i < activeBlobs.size(); i++) {
      Ellipse2D e = new Ellipse2D.Float(activeBlobs.get(i).getPos()[0], activeBlobs.get(i).getPos()[1], activeBlobs.get(i).getSemiAxes()[1],
          activeBlobs.get(i).getSemiAxes()[0]);
      graphics.rotate(activeBlobs.get(i).getRotAngle(), activeBlobs.get(i).getPos()[0] + 0.5 * activeBlobs.get(i).getSemiAxes()[1],
          activeBlobs.get(i).getPos()[1] + 0.5 * activeBlobs.get(i).getSemiAxes()[0]);
      graphics.draw(e);
      graphics.setTransform(old);
    }
    // showImage();
    ++imageCount;
    // TODO show a jFrame here
    ImageIO.write(bufferedImage, "png", UserHome.Pictures(String.format("example%03d.png", imageCount)));
    System.out.printf("Image saved as example%03d.png\n", imageCount);
  }
}
