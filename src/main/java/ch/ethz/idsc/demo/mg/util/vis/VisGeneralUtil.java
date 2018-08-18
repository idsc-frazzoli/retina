// code by mg
package ch.ethz.idsc.demo.mg.util.vis;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/** general visualization static methods */
public enum VisGeneralUtil {
  ;
  private static final byte CLEAR_BYTE = -1; // white

  /** saves BufferedImage in format "imagePrefix_imageCount_timeStamp.png" in folder at parentFilePath
   * 
   * @param bufferedImage
   * @param parentFilePath
   * @param logFilename
   * @param timeStamp interpreted as [s]
   * @param imageCount */
  public static void saveFrame(BufferedImage bufferedImage, File parentFilePath, String logFilename, double timeStamp, int imageCount) {
    int fileTimeStamp = (int) (1000 * timeStamp);
    try {
      String fileName = String.format("%s_%04d_%d.png", logFilename, imageCount, fileTimeStamp);
      ImageIO.write(bufferedImage, "png", new File(parentFilePath, fileName));
      System.out.printf("Image saved as %s\n", fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void saveFrame(BufferedImage bufferedImage, File parentFilePath, String logFilename, int imageCount) {
    try {
      String fileName = String.format("%s_%04d.png", logFilename, imageCount);
      ImageIO.write(bufferedImage, "png", new File(parentFilePath, fileName));
      System.out.printf("Image saved as %s\n", fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** sets bytes back to CLEAR_BYTE value
   * 
   * @param bytes representing frame content */
  public static void clearFrame(byte[] bytes) {
    // https://stackoverflow.com/questions/9128737/fastest-way-to-set-all-values-of-an-array
    Arrays.fill(bytes, CLEAR_BYTE);
  }

  /** saves screenshot of GUI
   * 
   * @param jFrame content */
  // TODO MG currently unused
  public static BufferedImage getGUIFrame(JFrame jFrame) {
    return new BufferedImage(jFrame.getContentPane().getWidth(), jFrame.getContentPane().getHeight(), BufferedImage.TYPE_INT_RGB);
  }
}
