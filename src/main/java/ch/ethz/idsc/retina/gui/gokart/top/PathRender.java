// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

import ch.ethz.idsc.owly.demo.se2.Se2CarIntegrator;
import ch.ethz.idsc.owly.demo.se2.Se2Controls;
import ch.ethz.idsc.owly.gui.GeometricLayer;
import ch.ethz.idsc.owly.gui.RenderInterface;
import ch.ethz.idsc.owly.math.flow.Flow;
import ch.ethz.idsc.owly.math.se2.Se2Utils;
import ch.ethz.idsc.owly.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owly.math.state.StateIntegrator;
import ch.ethz.idsc.owly.math.state.StateTime;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusEvent;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusListener;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sign;

class PathRender implements RenderInterface {
  private GokartStatusEvent gokartStatusEvent;
  public final GokartStatusListener gokartStatusListener = getEvent -> gokartStatusEvent = getEvent;

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(gokartStatusEvent) && gokartStatusEvent.isSteeringCalibrated()) {
      StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
          Se2CarIntegrator.INSTANCE, RationalScalar.of(1, 4), 4 * 5);
      // ---
      Scalar XAD = ChassisGeometry.GLOBAL.xAxleDistanceMeter(); // axle distance
      Scalar YHW = ChassisGeometry.GLOBAL.yHalfWidthMeter(); // half width
      final Tensor p1;
      final Tensor p2;
      final Scalar angle = gokartStatusEvent.getSteeringAngle();
      if (Sign.isPositive(angle)) {
        p1 = Tensors.of(RealScalar.ZERO, YHW, RealScalar.ONE);
        p2 = Tensors.of(XAD, YHW.negate(), RealScalar.ONE);
      } else {
        p1 = Tensors.of(XAD, YHW, RealScalar.ONE);
        p2 = Tensors.of(RealScalar.ZERO, YHW.negate(), RealScalar.ONE);
      }
      Scalar XAR = ChassisGeometry.GLOBAL.xAxleRearMeter();
      // center of rear axle
      StateTime CENTER = new StateTime(Tensors.of(XAR, RealScalar.ZERO, RealScalar.ZERO), RealScalar.ZERO);
      {
        final Flow flow_forward = Se2Controls.singleton(RealScalar.ONE, angle);
        final Tensor center_forward = //
            Tensor.of(stateIntegrator.trajectory(CENTER, flow_forward).stream().map(StateTime::state));
        Tensor w1 = Tensors.empty();
        Tensor w2 = Tensors.empty();
        for (Tensor x : center_forward) {
          Tensor pose = Se2Utils.toSE2Matrix(x);
          w1.append(pose.dot(p1));
          w2.append(pose.dot(p2));
        }
        graphics.setColor(new Color(0, 0, 255, 128));
        graphics.draw(geometricLayer.toPath2D(w1));
        graphics.draw(geometricLayer.toPath2D(w2));
      }
      {
        final Flow flow_reverse = Se2Controls.singleton(RealScalar.ONE.negate(), angle);
        final Tensor center_reverse = //
            Tensor.of(stateIntegrator.trajectory(CENTER, flow_reverse).stream().map(StateTime::state));
        Tensor w1 = Tensors.empty();
        Tensor w2 = Tensors.empty();
        for (Tensor x : center_reverse) {
          Tensor pose = Se2Utils.toSE2Matrix(x);
          w1.append(pose.dot(p1));
          w2.append(pose.dot(p2));
        }
        graphics.setColor(new Color(0, 0, 255, 128));
        graphics.draw(geometricLayer.toPath2D(w1));
        graphics.draw(geometricLayer.toPath2D(w2));
      }
    }
  }
}
