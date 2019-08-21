// code by jph
package ch.ethz.idsc.gokart.core.map;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.retina.app.map.ErodableMap;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

/** config file intended for 3rd generation "always-on" occupancy mapping */
public class OccupancyConfig {
  public static final OccupancyConfig GLOBAL = AppResources.load(new OccupancyConfig());
  /***************************************************/
  public final Scalar pixelWidth = Quantity.of(0.2, SI.METER);
  /** origin is of the form {px[m], py[m], angle}
   * 
   * dubendorf origin = {32[m], 20[m], pi/4} */
  public final Tensor origin = Tensors.of( //
      Quantity.of(32, SI.METER), //
      Quantity.of(20, SI.METER), //
      RealScalar.of(Math.PI / 4));
  public final Tensor extensions = Tensors.of( //
      Quantity.of(38.4, SI.METER), //
      Quantity.of(19.2, SI.METER));

  /***************************************************/
  public ErodableMap erodableMap() {
    Tensor dimension = Round.of(extensions.divide(pixelWidth));
    BufferedImage bufferedImage = new BufferedImage( //
        Scalars.intValueExact(dimension.Get(0)), //
        Scalars.intValueExact(dimension.Get(1)), //
        BufferedImage.TYPE_BYTE_GRAY);
    Tensor model2pixel = Dot.of( //
        PoseHelper.toSE2Matrix(origin), //
        DiagonalMatrix.of( //
            Magnitude.METER.apply(pixelWidth), //
            Magnitude.METER.apply(pixelWidth), RealScalar.ONE), //
        Se2Matrix.flipY(bufferedImage.getHeight()));
    return new ErodableMap(bufferedImage, model2pixel);
  }
}
