// code by mg
package ch.ethz.idsc.demo.mg.util;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
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

  /** scales a bufferedImage. if scaled width/height is smaller than 1, it is set to 1
   * 
   * @param unscaled original bufferedImage
   * @param scale scaling factor
   * @return scaled bufferedImage */
  public static BufferedImage scaleImage(BufferedImage unscaled, double scale) {
    int newWidth = (int) (unscaled.getWidth() * scale >= 1 ? unscaled.getWidth() * scale : 1);
    int newHeight = (int) (unscaled.getHeight() * scale >= 1 ? unscaled.getHeight() * scale : 1);
    BufferedImage scaled = new BufferedImage(newWidth, newHeight, unscaled.getType());
    AffineTransform scaleInstance = AffineTransform.getScaleInstance(scale, scale);
    AffineTransformOp scaleOp = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    scaleOp.filter(unscaled, scaled);
    return scaled;
  }

  /** flips the bufferedImage along the horizontal axis
   * 
   * @param bufferedImage
   * @return flipped bufferedImage */
  public static BufferedImage flipHorizontal(BufferedImage bufferedImage) {
    AffineTransform affineTransform = AffineTransform.getScaleInstance(1, -1);
    affineTransform.translate(0, -bufferedImage.getHeight());
    AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    return affineTransformOp.filter(bufferedImage, null);
  }
}
