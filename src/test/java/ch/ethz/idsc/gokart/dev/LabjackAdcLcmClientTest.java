// code by jph
package ch.ethz.idsc.gokart.dev;

import junit.framework.TestCase;

public class LabjackAdcLcmClientTest extends TestCase {
  public void testSimple() {
    LabjackAdcLcmClient labjackAdcLcmClient = new LabjackAdcLcmClient();
    labjackAdcLcmClient.startSubscriptions();
  }
}
