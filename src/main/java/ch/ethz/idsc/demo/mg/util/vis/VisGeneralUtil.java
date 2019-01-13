// code by mg
package ch.ethz.idsc.demo.mg.util.vis;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/** general visualization static methods */
public enum VisGeneralUtil {
  ;
  /** saves BufferedImage in format "logFilename_imageCount_timeStamp.png" in folder at parentFilePath
   * 
   * @param bufferedImage
   * @param directory
   * @param logFilename
   * @param timeStamp is converted to int for saving
   * @param imageCount */
  public static void saveFrame(BufferedImage bufferedImage, File directory, String logFilename, double timeStamp, int imageCount) {
    try {
      String filename = String.format("%s_%04d_%d.png", logFilename, imageCount, (int) timeStamp);
      ImageIO.write(bufferedImage, "png", new File(directory, filename));
      System.out.printf("Image saved as %s\n", filename);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
