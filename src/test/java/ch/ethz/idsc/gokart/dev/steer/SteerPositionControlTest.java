// code by rvmoos and jph
package ch.ethz.idsc.gokart.dev.steer;

import ch.ethz.idsc.gokart.calib.steer.HighPowerSteerPid;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.UserName;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class SteerPositionControlTest extends TestCase {
  public void _checkUnit(SteerPositionControl steerPositionControl) {
    steerPositionControl.iterate(Quantity.of(.1, SteerPutEvent.UNIT_ENCODER));
    steerPositionControl.iterate(Quantity.of(0, SteerPutEvent.UNIT_ENCODER));
    steerPositionControl.iterate(Quantity.of(0, SteerPutEvent.UNIT_ENCODER));
    for (int index = 0; index < 1000; ++index) {
      Scalar value = steerPositionControl.iterate(Quantity.of(0, SteerPutEvent.UNIT_ENCODER));
      SteerPutEvent.RTORQUE.apply(value);
    }
  }

  public void _checkRandom(SteerPositionControl steerPositionControl) {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 100; ++index) {
      Scalar err_pos = Quantity.of(RandomVariate.of(distribution), SteerPutEvent.UNIT_ENCODER);
      Scalar value = steerPositionControl.iterate(err_pos.multiply(RealScalar.of(0.01)));
      SteerPutEvent.RTORQUE.apply(value);
    }
  }

  public void _checkPosVel(SteerPositionControl steerPositionControl) {
    Scalar toAcc = Quantity.of(1, SteerPutEvent.UNIT_ENCODER.add(SI.PER_SECOND).add(SI.PER_SECOND));
    Scalar fromTorque = Quantity.of(1, SteerPutEvent.UNIT_RTORQUE.negate());
    Scalar torque2Acc = toAcc.multiply(fromTorque);
    Scalar wantedPos = Quantity.of(1, SteerPutEvent.UNIT_ENCODER);
    Scalar currentPos = Quantity.of(0, SteerPutEvent.UNIT_ENCODER);
    Scalar currentSpd = Quantity.of(0, SteerPutEvent.UNIT_ENCODER.add(SI.PER_SECOND));
    for (int index = 0; index < 10000; ++index) {
      Scalar torque = steerPositionControl.iterate(currentPos, wantedPos, Quantity.of(.1, SteerPutEvent.UNIT_ENCODER.add(SI.PER_SECOND)));
      SteerPutEvent.RTORQUE.apply(torque);
      currentSpd = currentSpd.add(torque.multiply(torque2Acc).multiply(SteerPositionControl.DT));
      currentPos = currentSpd.multiply(SteerPositionControl.DT);
      if (index % 1000 == 0 && UserName.is("datahaki")) {
        System.out.println("current Speed: " + currentSpd);
        System.out.println("current Position " + currentPos);
      }
    }
  }

  public void testUnit() {
    _checkUnit(new SteerPositionControl());
    _checkUnit(new SteerPositionControl(HighPowerSteerPid.GLOBAL));
  }

  public void testRandom() {
    _checkRandom(new SteerPositionControl());
    _checkRandom(new SteerPositionControl(HighPowerSteerPid.GLOBAL));
  }

  public void testSimple() {
    _checkPosVel(new SteerPositionControl());
    _checkPosVel(new SteerPositionControl(HighPowerSteerPid.GLOBAL));
  }

  public void testDt() {
    assertEquals(SteerPositionControl.DT, Quantity.of(0.02, "s"));
  }

  public void testFailNull() {
    try {
      new SteerPositionControl(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
