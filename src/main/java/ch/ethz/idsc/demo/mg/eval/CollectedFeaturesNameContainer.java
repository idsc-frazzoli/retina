// code by mg
package ch.ethz.idsc.demo.mg.eval;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;

// stores the varying names and pipelineConfigs for the tracking algorithm evaluation
// TODO load and store from file --> can be done elegant since we save Strings easily and
// PipelineConfigs through TensorProperties
// TODO use in pipelineSetup to save and EvaluatorMultiRun to load from
class CollectedFeaturesNameContainer {
  private final String[] estimatedLabelFileNames;
  private final PipelineConfig[] pipelineConfigs;

  CollectedFeaturesNameContainer(int iterationLength) {
    estimatedLabelFileNames = new String[iterationLength];
    pipelineConfigs = new PipelineConfig[iterationLength];
  }

  public void setEstimatedLabelFileName(String estimatedLabelFileName, int index) {
    estimatedLabelFileNames[index] = estimatedLabelFileName;
  }

  public void setPipelineConfig(PipelineConfig pipelineConfig, int index) {
    pipelineConfigs[index] = pipelineConfig;
  }

  public String getEstimatedLabelFileName(int index) {
    return estimatedLabelFileNames[index];
  }

  public PipelineConfig getPipelineConfig(int index) {
    return pipelineConfigs[index];
  }
}
