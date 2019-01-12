// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import ch.ethz.idsc.retina.u3.LabjackAdcFrame;
import junit.framework.TestCase;

public class GokartLabjackLcmClientTest extends TestCase {
  public static void publishOne() throws Exception {
    LabjackU3LcmModule labjackU3LcmModule = new LabjackU3LcmModule();
    labjackU3LcmModule.first();
    labjackU3LcmModule.labjackAdc(new LabjackAdcFrame(new float[] { 1f, 2f, 3f, 4f, 5f }));
    labjackU3LcmModule.last();
  }

  public void testSimple() throws Exception {
    GokartLabjackLcmClient labjackAdcLcmClient = new GokartLabjackLcmClient("asd", 0.2);
    labjackAdcLcmClient.start();
    publishOne();
    labjackAdcLcmClient.stop();
  }
}
