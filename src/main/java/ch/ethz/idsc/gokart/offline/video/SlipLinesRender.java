// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.calib.steer.RimoTireConfiguration;
import ch.ethz.idsc.gokart.calib.steer.RimoWheelConfigurations;
import ch.ethz.idsc.gokart.calib.steer.SteerColumnEvent;
import ch.ethz.idsc.gokart.calib.steer.SteerColumnEvents;
import ch.ethz.idsc.gokart.calib.steer.SteerColumnListener;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.gui.top.GokartRender;
import ch.ethz.idsc.owl.car.core.WheelConfiguration;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.AxisAlignedBox;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.util.BoundedLinkedList;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

/** draws brief history of rear axle center with orientation
 * to indicate drift in video playback */
/* package */ class SlipLinesRender implements GokartPoseListener, RenderInterface {
  private static final AxisAlignedBox AXIS_ALIGNED_BOX = //
      new AxisAlignedBox(RimoTireConfiguration._REAR.halfWidth().multiply(RealScalar.of(0.4)));

  private static class CombinedEvent {
    final GokartPoseEvent gokartPoseEvent;
    final Tensor matrix;
    final SteerColumnEvent steerColumnEvent;

    CombinedEvent(GokartPoseEvent gokartPoseEvent, SteerColumnEvent steerColumnEvent) {
      this.gokartPoseEvent = gokartPoseEvent;
      matrix = PoseHelper.toSE2Matrix(gokartPoseEvent.getPose());
      this.steerColumnEvent = steerColumnEvent;
    }
  }

  private final BoundedLinkedList<CombinedEvent> boundedLinkedList;
  SteerColumnEvent steerColumnEvent = SteerColumnEvents.UNKNOWN;
  SteerColumnListener steerColumnListener = getEvent -> steerColumnEvent = getEvent;

  public SlipLinesRender(int limit) {
    boundedLinkedList = new BoundedLinkedList<>(limit);
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    synchronized (boundedLinkedList) {
      boundedLinkedList.add(new CombinedEvent(gokartPoseEvent, steerColumnEvent));
    }
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    synchronized (boundedLinkedList) {
      for (CombinedEvent combinedEvent : boundedLinkedList) {
        geometricLayer.pushMatrix(combinedEvent.matrix);
        graphics.setStroke(new BasicStroke());
        // draw wheels
        if (combinedEvent.steerColumnEvent.isSteerColumnCalibrated()) {
          int count = 0;
          for (WheelConfiguration wheelConfiguration : RimoWheelConfigurations.fromSCE(combinedEvent.steerColumnEvent.getSteerColumnEncoderCentered())) {
            geometricLayer.pushMatrix(PoseHelper.toSE2Matrix(wheelConfiguration.local()));
            // draw slip
            Tensor tensor = wheelConfiguration.adjoint(combinedEvent.gokartPoseEvent.getVelocity());
            graphics.setColor(count < 2 //
                ? new Color(255, 128, 64, 192)
                : new Color(128, 255, 64, 192));
            graphics.fill(geometricLayer.toPath2D(AXIS_ALIGNED_BOX.alongY(tensor.Get(1).multiply(GokartRender.SLIP_FACTOR))));
            geometricLayer.popMatrix();
            ++count;
          }
        }
        geometricLayer.popMatrix();
      }
    }
  }
}
