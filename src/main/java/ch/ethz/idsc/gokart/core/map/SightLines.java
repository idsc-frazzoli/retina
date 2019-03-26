// code by gjoel
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.lidar.*;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16PolarProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.*;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.*;

/** class interprets sensor data from lidar */
public class SightLines extends AbstractSightLines {
    // TODO check rationale behind constant 10000!
    private static final int LIDAR_SAMPLES = 10000;
    private static final int SECTORS = 72;
    // ---
    private final LidarPolarFiringCollector lidarPolarFiringCollector = //
            new LidarPolarFiringCollector(LIDAR_SAMPLES, 3);
    private final Vlp16PolarProvider lidarPolarProvider = new Vlp16PolarProvider();
    private final LidarSectorProvider lidarSectorProvider = //
            new LidarSectorProvider(VelodyneStatics.AZIMUTH_RESOLUTION, SECTORS);
    private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
    private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
    private final TreeSet<Tensor> pointsPolar = //
            new TreeSet<>(Comparator.comparingDouble(point -> point.Get(0).number().doubleValue()));
    // ---
    private boolean isLaunched = true;
    private final int waitMillis;

    public static SightLines defaultGokart() {
        SightLines sightLines = new SightLines(SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate(), 200);
        sightLines.addBlindSpot(Tensors.vector(3., 3.4));
        sightLines.addBlindSpot(Tensors.vector(6.1, 0.2));
        return sightLines;
    }

    public SightLines(SpacialXZObstaclePredicate predicate, int waitMillis) {
        super(predicate);
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

    @Override // from Runnable
    public void run() {
        while (isLaunched) {
            Collection<Tensor> points = getClosestPoints();
            if (!points.isEmpty()) {
                synchronized (pointsPolar) {
                    pointsPolar.addAll(points);
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
        if (Objects.nonNull(gokartPoseEvent) && !pointsPolar.isEmpty()) {
            geometricLayer.pushMatrix(GokartPoseHelper.toSE2Matrix(gokartPoseEvent.getPose()));
            geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(SensorsConfig.GLOBAL.vlp16));
            // ---
            Tensor polygon = polygon();
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

    /** @return Tensor containing the current polygon points in cartesian coordinates */
    private Tensor polygon() {
        Tensor polygon;
        synchronized (pointsPolar) {
            polygon = polygon(pointsPolar);
            // ---
            double first = pointsPolar.first().Get(0).number().doubleValue();
            double last = pointsPolar.last().Get(0).number().doubleValue();
            if (Math.abs(last - first) < lidarSectorProvider.getSectorWidthRad())
                closeSector(polygon);
            // ---
            pointsPolar.clear();
        }
        return polygon;
    }
}