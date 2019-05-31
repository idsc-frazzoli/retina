// code by gjoel
package ch.ethz.idsc.gokart.core.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeSet;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.lidar.LidarPolarFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarSectorProvider;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16PolarProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Tensor;

/** class interprets sensor data from lidar */
public class SightLines extends AbstractLidarMapping implements RenderInterface {
  public static SightLines defaultGokart() {
    return new SightLines(SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate(), BlindSpots.defaultGokart(), 200);
  }

  // ---
  private final LidarPolarFiringCollector lidarPolarFiringCollector = //
      new LidarPolarFiringCollector(LIDAR_SAMPLES, 3);
  private final Vlp16PolarProvider lidarPolarProvider = new Vlp16PolarProvider();
  private final LidarSectorProvider lidarSectorProvider = //
      new LidarSectorProvider(VelodyneStatics.AZIMUTH_RESOLUTION, SightLineHandler.SECTORS);
  private final NavigableSet<Tensor> pointsPolar = //
      new TreeSet<>(Comparator.comparingDouble(point -> point.Get(0).number().doubleValue()));
  private final BlindSpots blindSpots;

  public SightLines(SpacialXZObstaclePredicate predicate, BlindSpots blindSpots, int waitMillis) {
    super(predicate, waitMillis);
    this.blindSpots = blindSpots;
    // ---
    lidarPolarProvider.setLimitLo(Magnitude.METER.toDouble(MappingConfig.GLOBAL.minDistance));
    lidarPolarProvider.addListener(lidarPolarFiringCollector);
    lidarSectorProvider.addListener(lidarPolarFiringCollector);
    lidarPolarFiringCollector.addListener(this);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarPolarProvider);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarSectorProvider);
  }

  @Override // from Runnable
  public void run() {
    while (isLaunched) {
      Collection<Tensor> points = SightLineHandler.getClosestPoints(points_ferry, spacialXZObstaclePredicate, blindSpots);
      if (!points.isEmpty()) {
        synchronized (pointsPolar) {
          pointsPolar.addAll(points);
        }
      } else {
        try {
          Thread.sleep(waitMillis);
        } catch (Exception exception) {
          // ---
        }
      }
    }
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (LocalizationConfig.GLOBAL.isQualityOk(gokartPoseEvent) && //
        !pointsPolar.isEmpty()) {
      geometricLayer.pushMatrix(PoseHelper.toSE2Matrix(gokartPoseEvent.getPose()));
      geometricLayer.pushMatrix(SensorsConfig.GLOBAL.vlp16Gokart());
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
    synchronized (pointsPolar) {
      Tensor polygon = SightLineHandler.polygon(pointsPolar);
      // ---
      double first = pointsPolar.first().Get(0).number().doubleValue();
      double last = pointsPolar.last().Get(0).number().doubleValue();
      if (Math.abs(last - first) < lidarSectorProvider.getSectorWidthRad())
        SightLineHandler.closeSector(polygon);
      // ---
      pointsPolar.clear();
      return polygon;
    }
  }
}