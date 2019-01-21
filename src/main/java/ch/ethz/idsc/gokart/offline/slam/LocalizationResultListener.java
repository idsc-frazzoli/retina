// code by jph
package ch.ethz.idsc.gokart.offline.slam;

@FunctionalInterface
public interface LocalizationResultListener {
  void localizationCallback(LocalizationResult localizationResult);
}
