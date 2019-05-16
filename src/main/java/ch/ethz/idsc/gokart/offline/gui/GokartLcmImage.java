// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
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
      tensor.append(ImageResize.nearest(auton.map(ColorDataGradients.AVOCADO), FX, 1));
    }
    {
      Tensor stact = Transpose.of(Tensor.of(gokartLogFileIndexer.raster2isSteerActive()));
      tensor.append(ImageResize.nearest(stact.map(ColorDataGradients.COPPER), FX, 1));
    }
    {
      Clip clip = Clips.interval(0.5, 1);
      Tensor poseq = Transpose.of(Tensor.of(gokartLogFileIndexer.raster2poseQuality()).map(clip::rescale));
      tensor.append(ImageResize.nearest(poseq.map(ColorDataGradients.AVOCADO), FX, 1));
    }
    {
      Scalar limit = SteerPutEvent.ENCODER.apply(SteerConfig.GLOBAL.columnMax);
      double value = limit.number().doubleValue();
      Clip clip = Clips.interval(-value, value);
      Tensor steer = Transpose.of(Tensor.of(gokartLogFileIndexer.raster2steerAngle()).map(clip::rescale));
      tensor.append(ImageResize.nearest(steer.map(ColorDataGradients.THERMOMETER), FX, 1));
    }
    {
      Scalar limit = SteerPutEvent.RTORQUE.apply(SteerConfig.GLOBAL.calibration);
      double value = limit.number().doubleValue();
      Clip clip = Clips.interval(-value, value);
      Tensor steer = Transpose.of(Tensor.of(gokartLogFileIndexer.raster2steerForce()).map(clip::rescale));
      tensor.append(ImageResize.nearest(steer.map(ColorDataGradients.THERMOMETER), FX, 1));
    }
    {
      Clip clip = Clips.interval(-1, +1);
      Tensor gyroz = Transpose.of(Tensor.of(gokartLogFileIndexer.raster2gyroZ()).map(clip::rescale));
      tensor.append(ImageResize.nearest(gyroz.map(ColorDataGradients.THERMOMETER), FX, 1));
    }
    {
      Clip clip = Clips.interval(0, 40);
      Tensor speed = Transpose.of(Tensor.of(gokartLogFileIndexer.raster2speed()).map(clip::rescale));
      tensor.append(ImageResize.nearest(speed.map(ColorDataGradients.CLASSIC), FX, 1));
    }
    BufferedImage bufferedImage = ImageFormat.of(Flatten.of(tensor, 1));
    System.out.println("image gen: " + timing.seconds());
    return bufferedImage;
  }
}
