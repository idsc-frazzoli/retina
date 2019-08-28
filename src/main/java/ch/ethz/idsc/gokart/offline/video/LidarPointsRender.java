// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.nio.FloatBuffer;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.owl.gui.ColorLookup;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.lidar.LidarPacketCollector;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneSpacialProvider;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16SegmentProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.util.BoundedLinkedList;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

/** shares code with OccupancyMappingCore */
/* package */ class LidarPointsRender implements RenderInterface, GokartPoseListener, LidarRayBlockListener {
  private static final class ColorShape {
    private final int color;
    private final int x;
    private final int y;

    public ColorShape(int color, int x, int y) {
      this.color = color;
      this.x = x;
      this.y = y;
    }
  }

  public final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  // private final int maxSize;
  private final BoundedLinkedList<ColorShape> boundedLinkedList;
  private final ColorDataIndexed colorLookup = ColorLookup.hsluv_lightness(128, 0.7);
  private final Tensor lidar2gokart = PoseHelper.toSE2Matrix(SensorsConfig.GLOBAL.vlp16_pose);
  private final GeometricLayer lidar2model = GeometricLayer.of(IdentityMatrix.of(3));
  // ---
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();

  public LidarPointsRender(Tensor model2pixel, int maxSize) {
    LidarPacketCollector lidarPacketCollector = new LidarPacketCollector(10_000, 3);
    VelodyneSpacialProvider velodyneSpacialProvider = //
        new Vlp16SegmentProvider(SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue(), -3);
    velodyneSpacialProvider.setLimitLo(Magnitude.METER.toDouble(MappingConfig.GLOBAL.minDistance));
    velodyneSpacialProvider.addListener(lidarPacketCollector);
    lidarPacketCollector.addListener(this);
    velodyneDecoder.addRayListener(velodyneSpacialProvider);
    velodyneDecoder.addRayListener(lidarPacketCollector);
    // ---
    boundedLinkedList = new BoundedLinkedList<>(maxSize);
    lidar2model.pushMatrix(model2pixel);
    lidar2model.pushMatrix(IdentityMatrix.of(3));
    lidar2model.pushMatrix(lidar2gokart);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    for (ColorShape colorShape : boundedLinkedList) {
      graphics.setColor(colorLookup.getColor(colorShape.color));
      graphics.fillRect(colorShape.x, colorShape.y, 1, 1);
    }
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    lidar2model.popMatrix();
    lidar2model.popMatrix();
    this.gokartPoseEvent = gokartPoseEvent;
    lidar2model.pushMatrix(PoseHelper.toSE2Matrix(gokartPoseEvent.getPose()));
    lidar2model.pushMatrix(lidar2gokart);
  }

  @Override // from LidarRayBlockListener
  public final void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    if (lidarRayBlockEvent.dimensions != 3)
      throw new RuntimeException("dim=" + lidarRayBlockEvent.dimensions);
    // ---
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    if (LocalizationConfig.GLOBAL.isQualityOk(gokartPoseEvent))
      for (int index = 0; index < lidarRayBlockEvent.size(); ++index)
        process( //
            floatBuffer.get(), //
            floatBuffer.get(), //
            floatBuffer.get());
    else
      System.err.println("pqual=" + gokartPoseEvent.getQuality());
  }

  private void process(float x, float y, float z) {
    Point2D point2d = lidar2model.toPoint2D(x, y);
    boundedLinkedList.add(new ColorShape( //
        Math.min(Math.max(0, 64 + (int) (z * 64)), 127), //
        (int) point2d.getX(), //
        (int) point2d.getY()));
  }
}
