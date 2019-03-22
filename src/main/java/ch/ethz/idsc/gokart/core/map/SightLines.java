// code by gjoel
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.lidar.*;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16PolarProvider;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Transform;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.*;
import ch.ethz.idsc.tensor.alg.Array;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.nio.FloatBuffer;
import java.util.*;

// TODO results only look satisfying due to aggregation over multiple rotations
// TODO take into account lidar blind spots (left, right)

/** class interprets sensor data from lidar */
public class SightLines implements //
        StartAndStoppable, LidarRayBlockListener, GokartPoseListener, RenderInterface, Runnable {
    // TODO check rationale behind constant 10000!
    private static final int LIDAR_SAMPLES = 10000;
    private static final int SECTORS = 72;
    private static final int HORIZON = 50; // in meters
    // ---
    private final LidarPolarFiringCollector lidarPolarFiringCollector = //
            new LidarPolarFiringCollector(LIDAR_SAMPLES, 3);
    private final Vlp16PolarProvider lidarPolarProvider = new Vlp16PolarProvider();
    private final LidarSectorProvider lidarSectorProvider = //
            new LidarSectorProvider(VelodyneStatics.AZIMUTH_RESOLUTION, SECTORS);
    private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
    private final Thread thread = new Thread(this);
    private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
    private final TreeMap<Scalar, Tensor> freeSpace = new TreeMap<>();
    private final TreeSet<Tensor> pointsPolar = //
            new TreeSet<>(Comparator.comparingDouble(point -> point.Get(0).number().doubleValue()));
    private final SpacialXZObstaclePredicate predicate;
    private final int waitMillis;
    // ---
    private GokartPoseEvent gokartPoseEvent;
    private boolean isLaunched = true;
    /** points_ferry is null or a matrix with dimension Nx3
     * containing the cross-section of the static geometry
     * with the horizontal plane at height of the lidar */
    private Tensor pointsPolar_ferry = null;

    public SightLines(SpacialXZObstaclePredicate predicate, int waitMillis) {
        this.predicate = predicate;
        this.waitMillis = waitMillis;
        // ---
        lidarPolarProvider.setLimitLo(Magnitude.METER.toDouble(MappingConfig.GLOBAL.minDistance));
        lidarPolarProvider.addListener(lidarPolarFiringCollector);
        lidarSectorProvider.addListener(lidarPolarFiringCollector);
        lidarPolarFiringCollector.addListener(this);
        gokartPoseLcmClient.addListener(this);
        vlp16LcmHandler.velodyneDecoder.addRayListener(lidarPolarProvider);
        vlp16LcmHandler.velodyneDecoder.addRayListener(lidarSectorProvider);
    }

    @Override // from StartAndStoppable
    public void start() {
        vlp16LcmHandler.startSubscriptions();
        gokartPoseLcmClient.startSubscriptions();
        thread.start();
        isLaunched = true;
    }

    @Override // from StartAndStoppable
    public void stop() {
        isLaunched = false;
        thread.interrupt();
        vlp16LcmHandler.stopSubscriptions();
        gokartPoseLcmClient.stopSubscriptions();
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

    @Override // from Runnable
    public void run() {
        while (isLaunched) {
            Tensor points = pointsPolar_ferry;
            if (Objects.nonNull(points) && Objects.nonNull(gokartPoseEvent)) {
                freeSpace.clear();
                for (Tensor point : points) { // point azimuth, elevation, radius
                    Scalar azimuth = point.Get(0);
                    if (!freeSpace.containsKey(azimuth))
                        freeSpace.put(azimuth, Tensors.vector(azimuth.number(), 0, HORIZON));
                    if (predicate.isObstacle(Vlp16Transform.PolarToCartesian.of(point))) {
                        Scalar distance = point.Get(2);
                        if (Scalars.lessThan(distance, freeSpace.get(azimuth).Get(2)))
                            freeSpace.put(azimuth, point);
                    }
                }
                synchronized (pointsPolar) {
                    pointsPolar.addAll(freeSpace.values());
                }
            } else {
                try {
                    Thread.sleep(waitMillis);
                } catch (Exception e) {
                    // ---
                }
            }
        }
    }

    @Override // from RenderInterface
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        if (Objects.nonNull(gokartPoseEvent)) {
            geometricLayer.pushMatrix(GokartPoseHelper.toSE2Matrix(gokartPoseEvent.getPose()));
            geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(SensorsConfig.GLOBAL.vlp16));
            // ---
            Tensor polygon = polygon(); // TODO make polygon creation independent of render
            // TODO apply filter? median, min, ...
            graphics.setColor(Color.RED);
            polygon.forEach(point -> {
                Point2D point2D = geometricLayer.toPoint2D(point);
                graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), 2, 2);
            });
            Path2D path2D = geometricLayer.toPath2D(polygon);
            path2D.closePath();
            graphics.setColor(new Color(0, 255, 0, 16));
            graphics.fill(path2D);
            graphics.setColor(new Color(0, 255, 0, 64));
            graphics.draw(path2D);
            // ---
            geometricLayer.popMatrix();
            geometricLayer.popMatrix();
        }
    }

    private Tensor polygon() {
        Tensor polygon;
        synchronized (pointsPolar) {
            polygon = Tensor.of(pointsPolar.stream().map(point -> //
                    Vlp16Transform.PolarToCartesian.of(point).extract(0, 2)));
            // ---
            double first = pointsPolar.first().Get(0).number().doubleValue();
            double last = pointsPolar.last().Get(0).number().doubleValue();
            if (Math.abs(last - first) < lidarSectorProvider.getSectorWidthRad())
                polygon.append(Array.zeros(2)); // add origin to close sector
            // ---
            pointsPolar.clear();
        }
        return polygon;
    }
}
