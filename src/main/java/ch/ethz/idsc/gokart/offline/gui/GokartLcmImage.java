// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ImageResize;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

public enum GokartLcmImage {
  ;
  static final int FX = 8;

  /** @param gokartLogFileIndexer
   * @return image indicating */
  public static BufferedImage of(GokartLogFileIndexer gokartLogFileIndexer) {
    Timing timing = Timing.started();
    Tensor tensor = Tensors.empty();
    {
      Tensor auton = Transpose.of(Tensor.of(gokartLogFileIndexer.raster2autoButton()));
      System.out.println("auton=" + Dimensions.of(auton));
      tensor.append(ImageResize.nearest(auton.map(ColorDataGradients.AVOCADO), FX, 1));
    }
    for (GokartLogImageRow gokartLogImageRow : gokartLogFileIndexer.gokartLogImageRows) {
      Tensor stact = Transpose.of(gokartLogImageRow.tensor());
      System.out.println("stact=" + Dimensions.of(stact));
      tensor.append(ImageResize.nearest(stact.map(gokartLogImageRow.getColorDataGradient()), FX, 1));
    }
    {
      Clip clip = Clips.interval(0.5, 1);
      Tensor poseq = Transpose.of(Tensor.of(gokartLogFileIndexer.raster2poseQuality()).map(clip::rescale));
      System.out.println("poseq=" + Dimensions.of(poseq));
      tensor.append(ImageResize.nearest(poseq.map(ColorDataGradients.AVOCADO), FX, 1));
    }
    {
      Scalar limit = SteerPutEvent.RTORQUE.apply(SteerConfig.GLOBAL.calibration);
      double value = limit.number().doubleValue();
      Clip clip = Clips.absolute(value);
      Tensor steer = Transpose.of(Tensor.of(gokartLogFileIndexer.raster2steerForce()).map(clip::rescale));
      tensor.append(ImageResize.nearest(steer.map(ColorDataGradients.THERMOMETER), FX, 1));
    }
    {
      Clip clip = Clips.absolute(+1);
      Tensor gyroz = Transpose.of(Tensor.of(gokartLogFileIndexer.raster2gyroZ()).map(clip::rescale));
      tensor.append(ImageResize.nearest(gyroz.map(ColorDataGradients.THERMOMETER), FX, 1));
    }
    {
      Clip clip = Clips.positive(40);
      Tensor speed = Transpose.of(Tensor.of(gokartLogFileIndexer.raster2speed()).map(clip::rescale));
      tensor.append(ImageResize.nearest(speed.map(ColorDataGradients.CLASSIC), FX, 1));
    }
    BufferedImage bufferedImage = ImageFormat.of(Flatten.of(tensor, 1));
    System.out.println("image gen: " + timing.seconds());
    return bufferedImage;
  }
}
