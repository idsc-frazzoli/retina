// code by jph
package ch.ethz.idsc.gokart.offline.cache;

import java.util.Arrays;

import ch.ethz.idsc.subare.util.RandomChoice;

public enum CachedLogs {
  ;
  /** @return file of less than ~70 MB */
  public static CachedLog randomSmall() {
    return RandomChoice.of(Arrays.asList( //
        CachedLog._20190401T115537_02, //
        CachedLog._20190404T143912_20, //
        CachedLog._20190404T143912_24, //
        CachedLog._20190404T143912_25, //
        CachedLog._20190701T174152_00));
  }
}
