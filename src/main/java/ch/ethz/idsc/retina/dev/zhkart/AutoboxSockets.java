// code by jph
package ch.ethz.idsc.retina.dev.zhkart;

import java.util.Arrays;
import java.util.Collection;

import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.misc.MiscSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;

public enum AutoboxSockets {
  ;
  public static final Collection<AutoboxSocket<?, ?>> ALL = Arrays.asList( //
      RimoSocket.INSTANCE, //
      LinmotSocket.INSTANCE, //
      SteerSocket.INSTANCE, //
      MiscSocket.INSTANCE);
}
