// code by jph
package ch.ethz.idsc.gokart.core;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

import ch.ethz.idsc.owl.ani.api.ProviderRank;

public enum ProviderRanks {
  ;
  private static final Map<ProviderRank, Color> MAP = new EnumMap<>(ProviderRank.class);
  static {
    for (ProviderRank providerRank : ProviderRank.values())
      MAP.put(providerRank, Color.WHITE); // white as default color
    // ---
    MAP.put(ProviderRank.HARDWARE /*     */, new Color(255, 128, 128)); // red
    MAP.put(ProviderRank.EMERGENCY /*    */, new Color(222, 195, 174)); // yellow/red
    MAP.put(ProviderRank.CALIBRATION /*  */, new Color(000, 255, 255)); // turquoise
    MAP.put(ProviderRank.MANUAL /*       */, new Color(128, 255, 128)); // green
    MAP.put(ProviderRank.TESTING /*      */, new Color(255, 255, 000)); // yellow
    MAP.put(ProviderRank.SAFETY /*       */, new Color(234, 222, 135)); // green yellow
    MAP.put(ProviderRank.AUTONOMOUS /*   */, new Color(255, 218, 218)); // light pink
    MAP.put(ProviderRank.FALLBACK /*     */, new Color(192, 192, 192)); // light gray
  }

  /** @param providerRank
   * @return color associated with given provider rank */
  public static Color color(ProviderRank providerRank) {
    return MAP.get(providerRank);
  }
}
