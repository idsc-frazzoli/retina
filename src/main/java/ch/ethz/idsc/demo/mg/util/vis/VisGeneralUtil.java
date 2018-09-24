// code by mg
package ch.ethz.idsc.demo.mg.util.vis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/** general visualization static methods */
public enum VisGeneralUtil {
  ;
  /** saves BufferedImage in format "logFilename_imageCount_timeStamp.png" in folder at parentFilePath
   * 
   * @param bufferedImage
   * @param parentFilePath
   * @param logFilename
   * @param timeStamp is converted to int for saving
   * @param imageCount */
  public static void saveFrame(BufferedImage bufferedImage, File parentFilePath, String logFilename, double timeStamp, int imageCount) {
    try {
      String fileName = String.format("%s_%04d_%d.png", logFilename, imageCount, (int) timeStamp);
      ImageIO.write(bufferedImage, "png", new File(parentFilePath, fileName));
      System.out.printf("Image saved as %s\n", fileName);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /** clears the bufferedImage to visualize a blank white rectangle
   * 
   * @param bufferedImage */
  public static void clearFrame(BufferedImage bufferedImage) {
    Graphics2D graphics2D = bufferedImage.createGraphics();
    graphics2D.setColor(Color.WHITE);
    graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
  }

  /** @param jFrame content
   * @return bufferedImage representing the GUI */
  // currently unused
  public static BufferedImage getGUIFrame(JFrame jFrame) {
    return new BufferedImage(jFrame.getContentPane().getWidth(), jFrame.getContentPane().getHeight(), BufferedImage.TYPE_INT_RGB);
  }
}
