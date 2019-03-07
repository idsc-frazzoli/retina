// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import java.util.Objects;
import java.util.Set;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class LinmotGetEventTest extends TestCase {
  public void testPosition() {
    LinmotGetEvent linmotGetEvent = LinmotGetHelper.createPos(123_000, 124_000);
    assertEquals(linmotGetEvent.getActualPosition(), Quantity.of(0.0123, "m"));
    assertEquals(linmotGetEvent.getDemandPosition(), Quantity.of(0.0124, "m"));
    assertTrue(Chop._13.close(linmotGetEvent.getPositionDiscrepancy(), Quantity.of(0.0001, "m")));
    Set<LinmotStatusWordBit> set = linmotGetEvent.getStatusWordBits();
    assertTrue(set.contains(LinmotStatusWordBit.OPERATION_ENABLED));
    LinmotStateVariable linmotStateVariable = linmotGetEvent.getStateVariable();
    assertEquals(linmotStateVariable.linmotStateVarMain, LinmotStateVarMain.OPERATION_ENABLED);
    assertEquals(linmotStateVariable.substate, 193);
  }

  public void testOperation() {
    LinmotGetEvent linmotGetEvent = LinmotGetHelper.createTemperature(500, 200);
    assertEquals(linmotGetEvent.length(), 16);
    linmotGetEvent.asArray();
    assertTrue(Objects.nonNull(linmotGetEvent.toInfoString()));
    assertEquals(linmotGetEvent.getActualPosition(), Quantity.of(1, "m"));
    assertEquals(linmotGetEvent.getWindingTemperature1(), Quantity.of(50, "degC"));
    assertEquals(linmotGetEvent.getWindingTemperature2(), Quantity.of(20, "degC"));
    assertTrue(linmotGetEvent.isOperational());
    Scalar temperature = linmotGetEvent.getWindingTemperatureMax();
    assertTrue(LinmotConfig.GLOBAL.isTemperatureOperationSafe(temperature));
    assertTrue(LinmotConfig.GLOBAL.isTemperatureHardwareSafe(temperature));
    assertEquals(linmotGetEvent.getWindingTemperatureMax(), Quantity.of(50, "degC"));
  }

  public void testHardware() {
    LinmotGetEvent linmotGetEvent = LinmotGetHelper.createTemperature(1000, 700);
    assertEquals(linmotGetEvent.length(), 16);
    linmotGetEvent.asArray();
    assertTrue(Objects.nonNull(linmotGetEvent.toInfoString()));
    assertEquals(linmotGetEvent.getActualPosition(), Quantity.of(1, "m"));
    assertEquals(linmotGetEvent.getWindingTemperature1(), Quantity.of(100, "degC"));
    assertEquals(linmotGetEvent.getWindingTemperature2(), Quantity.of(70, "degC"));
    assertTrue(linmotGetEvent.isOperational());
    Scalar temperature = linmotGetEvent.getWindingTemperatureMax();
    assertFalse(LinmotConfig.GLOBAL.isTemperatureOperationSafe(temperature));
    assertTrue(LinmotConfig.GLOBAL.isTemperatureHardwareSafe(temperature));
    assertEquals(linmotGetEvent.getWindingTemperatureMax(), Quantity.of(100, "degC"));
  }

  public void testFireworks() {
    LinmotGetEvent linmotGetEvent = LinmotGetHelper.createTemperature(1150, 900);
    assertEquals(linmotGetEvent.length(), 16);
    linmotGetEvent.asArray();
    assertTrue(Objects.nonNull(linmotGetEvent.toInfoString()));
    assertEquals(linmotGetEvent.getActualPosition(), Quantity.of(1, "m"));
    assertEquals(linmotGetEvent.getWindingTemperature1(), Quantity.of(115, "degC"));
    assertEquals(linmotGetEvent.getWindingTemperature2(), Quantity.of(90, "degC"));
    assertTrue(linmotGetEvent.isOperational());
    Scalar temperature = linmotGetEvent.getWindingTemperatureMax();
    assertFalse(LinmotConfig.GLOBAL.isTemperatureOperationSafe(temperature));
    assertFalse(LinmotConfig.GLOBAL.isTemperatureHardwareSafe(temperature));
    assertEquals(linmotGetEvent.getWindingTemperatureMax(), Quantity.of(115, "degC"));
  }
}
