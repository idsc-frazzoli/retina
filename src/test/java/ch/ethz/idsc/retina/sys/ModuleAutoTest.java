package ch.ethz.idsc.retina.sys;

import java.util.Arrays;

import ch.ethz.idsc.gokart.core.fuse.LinmotTakeoverModule;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240cDecoder;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ModuleAutoTest extends TestCase {
  public void testSimple() {
    Tensor t1 = Tensors.empty();
    Tensor t2 = Tensors.vector(1, 2, 3);
    assertTrue(t1.getClass() == t2.getClass()); // TensorImpl
    // TensorImpl != Tensor.class
  }

  public void testMore() {
    Davis240cDecoder d1 = new Davis240cDecoder();
    assertTrue(d1.getClass() == Davis240cDecoder.class);
  }

  public void testOne() throws Exception {
    ModuleAuto.INSTANCE.runOne(LinmotTakeoverModule.class);
    Thread.sleep(50); // needs time to start thread that invokes first()
    ModuleAuto.INSTANCE.terminateOne(LinmotTakeoverModule.class);
  }

  public void testAll() throws Exception {
    ModuleAuto.INSTANCE.runAll(Arrays.asList(LinmotTakeoverModule.class));
    Thread.sleep(100);
    ModuleAuto.INSTANCE.terminateAll();
  }
}
