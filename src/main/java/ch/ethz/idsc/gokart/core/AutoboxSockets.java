// code by jph
package ch.ethz.idsc.gokart.core;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.misc.MiscSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;

public enum AutoboxSockets {
  ;
  /** unmodifiable list of all AutoboxSocket instances */
  public static final List<AutoboxSocket<?, ?>> ALL = Arrays.asList( //
      LinmotSocket.INSTANCE, //
      SteerSocket.INSTANCE, //
      MiscSocket.INSTANCE, //
      RimoSocket.INSTANCE);
}
