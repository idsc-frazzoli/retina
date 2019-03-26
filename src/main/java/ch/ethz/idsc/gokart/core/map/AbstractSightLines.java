// code by gjoel
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.retina.lidar.*;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Transform;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.tensor.*;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Mod;

import java.nio.FloatBuffer;
import java.util.*;

/** class interprets sensor data from lidar */
/* package */ abstract class AbstractSightLines implements //
        StartAndStoppable, LidarRayBlockListener, GokartPoseListener, RenderInterface, Runnable {
    private static final int HORIZON = 50; // in meters
    // ---
    private Tensor pointsPolar_ferry = null;
    private Tensor blindSpots = Tensors.empty();
    // ---
    protected final Thread thread = new Thread(this);
    protected final SpacialXZObstaclePredicate predicate;
    protected GokartPoseEvent gokartPoseEvent;
    /** points_ferry is null or a matrix with dimension Nx3
     * containing the cross-section of the static geometry
     * with the horizontal plane at height of the lidar */

    public AbstractSightLines(SpacialXZObstaclePredicate predicate) {
        this.predicate = predicate;
    }

    @Override // from LidarRayBlockListener
    public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
        if (lidarRayBlockEvent.dimensions != 3)
            throw new RuntimeException("dim=" + lidarRayBlockEvent.dimensions);
        // ---
        FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
        pointsPolar_ferry = Tensors.vector(i -> Tensors.of( //
                DoubleScalar.of(floatBuffer.get()), //
                DoubleScalar.of(floatBuffer.get()), //
                DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
        thread.interrupt();
    }

    @Override // from GokartPoseListener
    public void getEvent(GokartPoseEvent getEvent) {
        gokartPoseEvent = getEvent;
    }

    protected Collection<Tensor> getClosestPoints() {
        Tensor points = pointsPolar_ferry;
        TreeMap<Scalar, Tensor> freeSpace = new TreeMap<>();
        if (Objects.nonNull(points) && Objects.nonNull(gokartPoseEvent))
            for (Tensor point : points) { // point azimuth, elevation, radius
                Scalar azimuth = point.Get(0);
                if (!isBlind(azimuth)) {
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

    protected Tensor polygon(Collection<Tensor> pointsPolar) {
        return Tensor.of(pointsPolar.stream().map(point -> //
                Vlp16Transform.PolarToCartesian.of(point).extract(0, 2)));
    }

    protected Tensor closeSector(Tensor polygon) {
        return polygon.append(Array.zeros(2)); // add origin to close sector
    }

    public void addBlindSpot(Tensor vector) {
        blindSpots.append(vector.map(Mod.function(Pi.TWO)::of));
    }

    private boolean isBlind(Scalar azimuth) {
        return blindSpots.stream().anyMatch(sector -> {
            Scalar start = sector.Get(0);
            Scalar end = sector.Get(1);
            if (Scalars.lessEquals(start, end)) {
                return Scalars.lessEquals(start, azimuth) && Scalars.lessEquals(azimuth, end);
            } else {
                return (Scalars.lessEquals(RealScalar.ZERO, azimuth) && Scalars.lessEquals(azimuth, end)) || //
                        (Scalars.lessEquals(start, azimuth) && Scalars.lessEquals(azimuth, Pi.TWO));
            }
        });
    }
}