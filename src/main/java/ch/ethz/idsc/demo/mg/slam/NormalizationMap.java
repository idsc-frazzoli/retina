// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.tensor.Tensor;

// provides the normalization map
public class NormalizationMap {
  NormalizationMap() {
    // initialize array to zero
  }

  public void update(Tensor currentExpectedState, Tensor lastExpectedState) {
    // first, find all map locations that are visible on the image plane using ImageToWorldLookup
    // project it onto the image plane using the current and the last expectedstate
    // compute norm between those two
    // TODO think on how to handle map locations that come or leave the field of view
    // NOTE: map location close to the sensor will have larger movements on the image plane than far away locations
    // therefore, we normalize the fact that far away features do not generate as many events
  }
}
