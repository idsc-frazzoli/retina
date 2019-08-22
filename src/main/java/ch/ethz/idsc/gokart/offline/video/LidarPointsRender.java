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
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.Hue;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

/** shares code with OccupancyMappingCore */
/* package */ class LidarPointsRender implements RenderInterface, GokartPoseListener, LidarRayBlockListener {
  public final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final int maxSize;
  private final BoundedLinkedList<Tensor> boundedLinkedList;
  private final Tensor lidar2gokart = PoseHelper.toSE2Matrix(SensorsConfig.GLOBAL.vlp16_pose);
  private final GeometricLayer lidar2model = GeometricLayer.of(IdentityMatrix.of(3));
  // ---
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();

  public LidarPointsRender(int maxSize) {
    this.maxSize = maxSize;
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
    lidar2model.pushMatrix(IdentityMatrix.of(3));
    lidar2model.pushMatrix(lidar2gokart);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    int count = 0;
    for (Tensor point : boundedLinkedList) {
      Point2D point2d = geometricLayer.toPoint2D(point);
      graphics.setColor(Hue.of(0.6, 1, 1, (maxSize - count) / (double) maxSize));
      graphics.fillRect( //
          (int) point2d.getX(), //
          (int) point2d.getY(), 1, 1);
      ++count;
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
    process(Tensors.vector(i -> Tensors.of( //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size()));
  }

  private void process(Tensor points) {
    if (LocalizationConfig.GLOBAL.isQualityOk(gokartPoseEvent))
      for (Tensor point : points) // point x, y, z
        boundedLinkedList.add(lidar2model.toVector(point));
  }
}
