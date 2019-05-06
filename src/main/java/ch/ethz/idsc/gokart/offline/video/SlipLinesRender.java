// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.calib.steer.GokartStatusEvents;
import ch.ethz.idsc.gokart.calib.steer.RimoTireConfiguration;
import ch.ethz.idsc.gokart.calib.steer.RimoWheelConfigurations;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.GokartStatusListener;
import ch.ethz.idsc.gokart.gui.top.AxisAlignedBox;
import ch.ethz.idsc.gokart.gui.top.GokartRender;
import ch.ethz.idsc.owl.car.core.WheelConfiguration;
import ch.ethz.idsc.owl.data.BoundedLinkedList;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

class CombinedEvent {
  final GokartPoseEvent gokartPoseEvent;
  final Tensor matrix;
  final GokartStatusEvent gokartStatusEvent;

  CombinedEvent(GokartPoseEvent gokartPoseEvent, GokartStatusEvent gokartStatusEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
    matrix = PoseHelper.toSE2Matrix(gokartPoseEvent.getPose());
    this.gokartStatusEvent = gokartStatusEvent;
  }
}

/** draws brief history of rear axle center with orientation
 * to indicate drift in video playback */
/* package */ class SlipLinesRender implements GokartPoseListener, RenderInterface {
  private static final AxisAlignedBox AXIS_ALIGNED_BOX = //
      new AxisAlignedBox(RimoTireConfiguration._REAR.halfWidth().multiply(RealScalar.of(0.4)));
  // ---
  private final BoundedLinkedList<CombinedEvent> boundedLinkedList;
  GokartStatusEvent gokartStatusEvent = GokartStatusEvents.UNKNOWN;
  GokartStatusListener gokartStatusListener = getEvent -> gokartStatusEvent = getEvent;

  public SlipLinesRender(int limit) {
    boundedLinkedList = new BoundedLinkedList<>(limit);
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    synchronized (boundedLinkedList) {
      boundedLinkedList.add(new CombinedEvent(gokartPoseEvent, gokartStatusEvent));
    }
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    synchronized (boundedLinkedList) {
      for (CombinedEvent combinedEvent : boundedLinkedList) {
        geometricLayer.pushMatrix(combinedEvent.matrix);
        graphics.setStroke(new BasicStroke());
        // draw wheels
        if (combinedEvent.gokartStatusEvent.isSteerColumnCalibrated()) {
          int count = 0;
          for (WheelConfiguration wheelConfiguration : RimoWheelConfigurations.fromSCE(combinedEvent.gokartStatusEvent.getSteerColumnEncoderCentered())) {
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
