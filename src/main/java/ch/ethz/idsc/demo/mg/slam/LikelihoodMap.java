// code by mg
package ch.ethz.idsc.demo.mg.slam;

// provides likelihood map
public class LikelihoodMap {
  // fields: array that represents discretized map
  LikelihoodMap() {
    // initialize array to zero
  }

  //
  public void update(OccurrenceMap occurrenceMap, NormalizationMap normalizationMap) {
    // apply simple division of occurrenceMap by normalizationMap
    // If normalizationmap is zero, then occurrenceMap (likely) zero as well and likelihoodMap is zero at that position
    // iterate through all map locations
  }
}
