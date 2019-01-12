// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import ch.ethz.idsc.retina.u3.LabjackAdcFrame;
import junit.framework.TestCase;

public class LabjackU3LcmModuleTest extends TestCase {
  public void testSimple() throws Exception {
    LabjackU3LcmModule labjackU3LcmModule = new LabjackU3LcmModule();
    labjackU3LcmModule.first();
    labjackU3LcmModule.labjackAdc(new LabjackAdcFrame(new float[2])); // not for gokart !
    labjackU3LcmModule.labjackAdc(new LabjackAdcFrame(new float[5]));
    labjackU3LcmModule.labjackAdc(new LabjackAdcFrame(new float[10])); // not for gokart !
    labjackU3LcmModule.last();
  }

  public void testStatic() throws Exception {
    LabjackU3LcmModule.accept(new LabjackAdcFrame(new float[5]));
  }
}
