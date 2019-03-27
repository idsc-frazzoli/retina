// code by gjoel
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Transform;
import ch.ethz.idsc.tensor.*;
import ch.ethz.idsc.tensor.alg.Array;

import java.util.*;

/* package */ enum SightLineHandler {
    ;

    public static final int HORIZON = 50; // in meters
    public static final int SECTORS = 72;

    /**
     *
     * @param points in polar coordinates recorded by lidar
     * @param predicate obstacle detection
     * @param blindSpots of lidar
     * @return Collection</Tensor> of closest points per azimuth in pointsPolar_ferry */
    public static Collection<Tensor> getClosestPoints( //
            Tensor points, SpacialXZObstaclePredicate predicate, BlindSpots blindSpots) {
        TreeMap<Scalar, Tensor> freeSpace = new TreeMap<>();
        if (Objects.nonNull(points))
            for (Tensor point : points) { // point azimuth, elevation, radius
                Scalar azimuth = point.Get(0);
                if (!blindSpots.isBlind(azimuth)) {
                    if (!freeSpace.containsKey(azimuth))
                        freeSpace.put(azimuth, Tensors.vector(azimuth.number(), 0, HORIZON));
                    if (predicate.isObstacle(Vlp16Transform.PolarToCartesian.of(point))) {
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
        return Tensor.of(pointsPolar.stream().map(point -> //
                Vlp16Transform.PolarToCartesian.of(point).extract(0, 2)));
    }

    /** close segment with origin to full sector
     * @param polygon Tensor containing cartesian points
     * @return Tensor containing cartesian points */
    public static Tensor closeSector(Tensor polygon) {
        return polygon.append(Array.zeros(2)); // add origin to close sector
    }
}