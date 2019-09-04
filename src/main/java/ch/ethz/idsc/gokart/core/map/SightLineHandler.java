// code by gjoel
package ch.ethz.idsc.gokart.core.map;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ enum SightLineHandler {
  ;
  public static final int HORIZON = 50; // in meters
  public static final int SECTORS = 72;
  private static final TensorUnaryOperator FROM_POLAR = SensorsConfig.GLOBAL.vlp16FromPolarCoordinates();
  private static final Tensor ZEROS_2 = Array.zeros(2);

  /** @param points in polar coordinates recorded by lidar
   * @param predicate obstacle detection
   * @param blindSpots of lidar
   * @return Collection</Tensor> of closest points per azimuth in pointsPolar_ferry */
  public static Collection<Tensor> getClosestPoints( //
      Tensor points, SpacialXZObstaclePredicate predicate, BlindSpots blindSpots) {
    Map<Scalar, Tensor> freeSpace = new TreeMap<>();
    if (Objects.nonNull(points))
      for (Tensor point : points) { // point azimuth, elevation, radius
        Scalar azimuth = point.Get(0);
        if (!blindSpots.isBlind(azimuth)) {
          if (!freeSpace.containsKey(azimuth))
            freeSpace.put(azimuth, Tensors.vector(azimuth.number(), 0, HORIZON));
          if (predicate.isObstacle(FROM_POLAR.apply(point))) {
            Scalar distance = point.Get(2);
            if (Scalars.lessThan(distance, freeSpace.get(azimuth).Get(2)))
              freeSpace.put(azimuth, point);
          }
        } else {
          freeSpace.put(azimuth, Tensors.of(azimuth, RealScalar.ZERO, RealScalar.ZERO));
        }
      }
    return freeSpace.values();
  }

  /** @param pointsPolar Collection</Tensor>
   * @return Tensor containing cartesian points */
  public static Tensor polygon(Collection<Tensor> pointsPolar) {
    return Tensor.of(pointsPolar.stream().map(FROM_POLAR).map(Extract2D.FUNCTION));
  }

  /** close segment with origin to full sector
   * @param polygon Tensor containing cartesian points
   * @return Tensor containing cartesian points */
  public static Tensor closeSector(Tensor polygon) {
    return polygon.append(ZEROS_2); // add origin to close sector
  }
}