// code by jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.gokart.gui.top.PredefinedMap;

public enum DubendorfLocalization {
  INSTANCE;
  // ---
  public final PredefinedMap predefinedMap = PredefinedMap.DUBENDORF_HANGAR_20180122;
  public final LidarGyroLocalization lidarGyroLocalization = new LidarGyroLocalization(predefinedMap);
}
