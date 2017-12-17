// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pure;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class PurePursuitModuleTest extends TestCase {
  public void testFirstLast() throws Exception {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    purePursuitModule.first();
    purePursuitModule.runAlgo();
    purePursuitModule.last();
  }

  public void testSimple() throws Exception {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = new GokartPoseEvent(Tensors.fromString("{0[m],0[m],0}"));
    purePursuitModule.getEvent(gokartPoseEvent);
    assertFalse(purePursuitModule.purePursuitSteer.isOperational());
    assertFalse(purePursuitModule.purePursuitRimo.isOperational());
    assertFalse(purePursuitModule.purePursuitSteer.putEvent().isPresent());
    assertFalse(purePursuitModule.purePursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testClose() throws Exception {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = new GokartPoseEvent(Tensors.fromString("{35.1[m], 44.9[m], 1}"));
    purePursuitModule.getEvent(gokartPoseEvent);
    purePursuitModule.runAlgo();
    assertTrue(purePursuitModule.purePursuitSteer.isOperational());
    assertTrue(purePursuitModule.purePursuitRimo.isOperational());
    assertFalse(purePursuitModule.purePursuitSteer.putEvent().isPresent());
    assertFalse(purePursuitModule.purePursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testSpecific() throws Exception {
    Tensor pose = Tensors.fromString("{35.1[m], 44.9[m], 1}");
    Optional<Tensor> optional = PurePursuitModule.getLookAhead(pose, DubendorfCurve.OVAL);
    Tensor lookAhead = optional.get();
    System.out.println(lookAhead);
    Scalar angle = ArcTan.of(lookAhead.Get(0), lookAhead.Get(1));
    assertTrue(Clip.function(-0.1, 0).isInside(angle));
  }

  public void testSpecific2() throws Exception {
    Tensor pose = Tensors.fromString("{35.1[m], 44.9[m], 0.9}");
    Optional<Tensor> optional = PurePursuitModule.getLookAhead(pose, DubendorfCurve.OVAL);
    Tensor lookAhead = optional.get();
    System.out.println(lookAhead);
    Scalar angle = ArcTan.of(lookAhead.Get(0), lookAhead.Get(1));
    // System.out.println(angle);
    assertTrue(Clip.function(0, 0.1).isInside(angle));
  }

  public void testPeriod() {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    double v1 = purePursuitModule.getPeriod();
    double v2 = PursuitConfig.GLOBAL.updatePeriodSeconds().number().doubleValue();
    assertEquals(v1, v2);
  }
}
