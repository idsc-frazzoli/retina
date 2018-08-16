// code by mg
package ch.ethz.idsc.demo.mg.util.vis;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/** provides general visualization static methods */
// TODO MG if functionality will only be used in SlamViewer, move to that package and reduce visibility
public enum VisGeneralUtil {
  ;
  /** saves provided BufferedImage in format "imagePrefix_imageCount_timeStamp.png" at the provided file path
   * 
   * @param bufferedImage
   * @param parentFilePath
   * @param imagePrefix
   * @param timeStamp interpreted as [s]
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
