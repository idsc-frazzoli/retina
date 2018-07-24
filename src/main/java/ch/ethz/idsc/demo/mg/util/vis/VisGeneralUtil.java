// code by mg
package ch.ethz.idsc.demo.mg.util.vis;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

// provides general visualization static methods
public enum VisGeneralUtil {
  ;
  /** saves provided BufferedImage
   * 
   * @param bufferedImage
   * @param parentFilePath
   * @param imagePrefix
   * @param timeStamp
   * @param imageCount */
  public static void saveFrame(BufferedImage bufferedImage, File parentFilePath, String imagePrefix, double timeStamp, int imageCount) {
    int fileTimeStamp = (int) (1000 * timeStamp);
    try {
      String fileName = String.format("%s_%04d_%d.png", imagePrefix, imageCount, fileTimeStamp);
      ImageIO.write(bufferedImage, "png", new File(parentFilePath, fileName));
      System.out.printf("Image saved as %s\n", fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
