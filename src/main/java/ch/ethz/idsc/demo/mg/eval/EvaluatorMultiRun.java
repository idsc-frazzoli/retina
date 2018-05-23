// code by mg
package ch.ethz.idsc.demo.mg.eval;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;

/** compares a bunch of estimated runs against the ground truth by initializing one TrackingEvaluatorSingleRun per
 * estimated run. */
public class EvaluatorMultiRun {
  private final List<double[]> collectedResults;
  private final int iterationLength;
  private PipelineConfig pipelineConfig;

  EvaluatorMultiRun(PipelineConfig pipelineConfig) {
    this.pipelineConfig = pipelineConfig;
    iterationLength = pipelineConfig.iterationLength.number().intValue();
    collectedResults = new ArrayList<>(iterationLength);
  }

  private void multiRun() {
    for (int i = 0; i < iterationLength; i++) {
      // to initialize singleRun, only the estimatedLabelFileName needs to be changed
      // TODO this needs to be similar to the fileNames defined in PipelineSetup::iterate() maybe there is a more elegant option
      int newTau = 1000 + 1000 * i;
      String newEstimatedLabelFileName = pipelineConfig.logFileName.toString() + "_tau_" + newTau;
      pipelineConfig.estimatedLabelFileName = newEstimatedLabelFileName;
      // initialize singleRun object and run evaluation
      EvaluatorSingleRun singleRun = new EvaluatorSingleRun(pipelineConfig);
      singleRun.runEvaluation();
      // collect results
      // TODO save all results to file which then allows easy plotting
      collectedResults.add(singleRun.getResults());
    }
  }

  private void summarizeResults() {
    for (int i = 0; i < iterationLength; i++) {
      System.out.println("average recall is " + collectedResults.get(i)[0]);
      System.out.println("average precision is " + collectedResults.get(i)[1]);
    }
  }

  // standalone application
  public static void main(String[] args) {
    PipelineConfig pipelineConfig = new PipelineConfig();
    EvaluatorMultiRun test = new EvaluatorMultiRun(pipelineConfig);
    test.multiRun();
    test.summarizeResults();
  }
}
