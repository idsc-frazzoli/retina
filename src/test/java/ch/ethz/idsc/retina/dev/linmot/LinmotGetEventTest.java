// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class LinmotGetEventTest extends TestCase {
  public void testOperation() {
    LinmotGetEvent linmotGetEvent = LinmotGetEventSimulator.create(500, 200);
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
    LinmotGetEvent linmotGetEvent = LinmotGetEventSimulator.create(1000, 700);
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
    LinmotGetEvent linmotGetEvent = LinmotGetEventSimulator.create(1150, 900);
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
