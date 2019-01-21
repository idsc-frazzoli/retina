// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.GokartStatusListener;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.Sign;

/** draw blue lines of prediction of traces of gokart */
// TODO JPH/MH need better class name
public class GokartPathRender extends AbstractGokartRender {
  public Color color = new Color(0, 0, 255, 128);
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  private GokartStatusEvent gokartStatusEvent;
  public final GokartStatusListener gokartStatusListener = getEvent -> gokartStatusEvent = getEvent;

  public GokartPathRender(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
  }

  @Override
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(gokartStatusEvent) && gokartStatusEvent.isSteerColumnCalibrated()) {
      StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
          Se2CarIntegrator.INSTANCE, RationalScalar.of(1, 4), 4 * 5);
      // ---
      Scalar XAD = ChassisGeometry.GLOBAL.xAxleDistanceMeter(); // axle distance
      Scalar YHW = ChassisGeometry.GLOBAL.yHalfWidthMeter(); // half width
      final Tensor p1;
      final Tensor p2;
      final Scalar angle = steerMapping.getAngleFromSCE(gokartStatusEvent); // <- calibration checked
      if (Sign.isPositive(angle)) {
        p1 = Tensors.of(RealScalar.ZERO, YHW, RealScalar.ONE);
        p2 = Tensors.of(XAD, YHW.negate(), RealScalar.ONE);
      } else {
        p1 = Tensors.of(XAD, YHW, RealScalar.ONE);
        p2 = Tensors.of(RealScalar.ZERO, YHW.negate(), RealScalar.ONE);
      }
      // center of rear axle
      StateTime CENTER = new StateTime(Tensors.of(RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO), RealScalar.ZERO);
      {
        final Flow flow_forward = singleton(RealScalar.ONE, angle);
        final Tensor center_forward = //
            Tensor.of(stateIntegrator.trajectory(CENTER, flow_forward).stream().map(StateTime::state));
        Tensor w1 = Tensors.empty();
        Tensor w2 = Tensors.empty();
        for (Tensor x : center_forward) {
          Tensor pose = Se2Utils.toSE2Matrix(x);
          w1.append(pose.dot(p1));
          w2.append(pose.dot(p2));
        }
        graphics.setColor(color);
        graphics.draw(geometricLayer.toPath2D(w1));
        graphics.draw(geometricLayer.toPath2D(w2));
      }
      {
        final Flow flow_reverse = singleton(RealScalar.ONE.negate(), angle);
        final Tensor center_reverse = //
            Tensor.of(stateIntegrator.trajectory(CENTER, flow_reverse).stream().map(StateTime::state));
        Tensor w1 = Tensors.empty();
        Tensor w2 = Tensors.empty();
        for (Tensor x : center_reverse) {
          Tensor pose = Se2Utils.toSE2Matrix(x);
          w1.append(pose.dot(p1));
          w2.append(pose.dot(p2));
        }
        graphics.setColor(color);
        graphics.draw(geometricLayer.toPath2D(w1));
        graphics.draw(geometricLayer.toPath2D(w2));
      }
    }
  }

  /* package for testing */ static Flow singleton(Scalar speed, Tensor rate) {
    return StateSpaceModels.createFlow(Se2StateSpaceModel.INSTANCE, //
        N.DOUBLE.of(Tensors.of(speed, RealScalar.ZERO, rate.multiply(speed))));
  }
}
