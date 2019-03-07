// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.gokart.dev.misc.MiscGetEvent;
import ch.ethz.idsc.gokart.dev.misc.MiscPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.MiscLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.SteerLcmServer;

public enum OfflineVectorTables {
  ;
  public static OfflineVectorTable linmotGet() {
    return new OfflineVectorTable(LinmotLcmServer.CHANNEL_GET, LinmotGetEvent::new);
  }

  public static OfflineVectorTable linmotPut() {
    return new OfflineVectorTable(LinmotLcmServer.CHANNEL_PUT, LinmotPutEvent::new);
  }

  /***************************************************/
  public static OfflineVectorTable miscGet() {
    return new OfflineVectorTable(MiscLcmServer.CHANNEL_GET, MiscGetEvent::new);
  }

  public static OfflineVectorTable miscPut() {
    return new OfflineVectorTable(MiscLcmServer.CHANNEL_PUT, MiscPutEvent::new);
  }

  /***************************************************/
  public static OfflineVectorTable steerGet() {
    return new OfflineVectorTable(SteerLcmServer.CHANNEL_GET, SteerGetEvent::new);
  }

  public static OfflineVectorTable steerPut() {
    return new OfflineVectorTable(SteerLcmServer.CHANNEL_PUT, SteerPutEvent::from);
  }

  /***************************************************/
  public static OfflineVectorTable rimoGet() {
    return new OfflineVectorTable(RimoLcmServer.CHANNEL_GET, RimoGetEvent::new);
  }

  public static OfflineVectorTable rimoPut() {
    return new OfflineVectorTable(RimoLcmServer.CHANNEL_PUT, RimoPutHelper::from);
  }
}
