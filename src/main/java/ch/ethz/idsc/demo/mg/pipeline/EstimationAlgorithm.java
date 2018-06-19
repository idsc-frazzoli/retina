// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.util.List;

// estimates the location of features in physical space based on measurements and process model
// TODO set up simple visualization for this module
public class EstimationAlgorithm {
  // fields
  List<PhysicalBlob> measurementBlobs;
  List<PhysicalBlob> estimatedBlobs; // TODO maybe create new class for estimation of features

  EstimationAlgorithm() {
    // ...
  }

  // call this function for a measurement update. how often to call this function?
  private void setMeasurement(List<PhysicalBlob> measurementBlobs) {
    this.measurementBlobs = measurementBlobs;
  }

  // process model
  private void predictionStep() {
    // we start simple: measurement is process model
    estimatedBlobs = measurementBlobs;
  }

  List<PhysicalBlob> getEstimatedBlobs() {
    return estimatedBlobs;
  }
}
