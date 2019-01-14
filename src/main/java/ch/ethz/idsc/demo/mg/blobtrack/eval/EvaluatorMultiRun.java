// code by mg
package ch.ethz.idsc.demo.mg.blobtrack.eval;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.blobtrack.BlobTrackConfig;

/** compares a bunch of estimated runs against the ground truth by initializing one TrackingEvaluatorSingleRun per
 * estimated run. */
/* package */ class EvaluatorMultiRun {
  private final BlobTrackConfig pipelineConfig;
  private final String evaluationResultFileName;
  private final File evaluationResultFile;
  private final List<double[]> collectedResults;
  private final int iterationLength;

  EvaluatorMultiRun(BlobTrackConfig pipelineConfig) {
    this.pipelineConfig = pipelineConfig;
    evaluationResultFileName = pipelineConfig.evaluationResultFileName.toString();
    evaluationResultFile = EvaluationFileLocations.EVALRESULTS_CSV.subfolder(evaluationResultFileName);
    iterationLength = pipelineConfig.iterationLength.number().intValue();
    collectedResults = new ArrayList<>(iterationLength);
  }

  private void multiRun() {
    for (int i = 0; i < iterationLength; i++) {
      // to initialize singleRun, only the estimatedLabelFileName needs to be changed
      // TODO MG this needs to be similar to the fileNames defined in PipelineSetup::iterate() maybe there is a more elegant option
      int newTau = 1000 + 1000 * i;
      String newEstimatedLabelFileName = pipelineConfig.davisConfig.logFilename() + "_tau_" + newTau;
      // TODO MG filename not generic. perhaps pass in "Dubi15a_tau" as argument?
      pipelineConfig.estimatedLabelFileName = "Dubi15a_tau/" + newEstimatedLabelFileName;
      // initialize singleRun object and run evaluation
      EvaluatorSingleRun singleRun = new EvaluatorSingleRun(pipelineConfig);
      singleRun.runEvaluation();
      // collect results
      double[] results = new double[] { newTau, singleRun.getResults()[0], singleRun.getResults()[1] };
      collectedResults.add(results);
    }
    // TODO MG probably also save pipelineConfig that was used?
    EvalUtil.saveToCSV(evaluationResultFile, collectedResults);
    System.out.println("Successfully saved evaluation results to " + evaluationResultFileName);
  }

  private void summarizeResults() {
    for (int i = 0; i < iterationLength; i++) {
      System.out.println("average recall is " + collectedResults.get(i)[1]);
      System.out.println("average precision is " + collectedResults.get(i)[2]);
    }
  }

  // standalone application
  public static void main(String[] args) {
    BlobTrackConfig pipelineConfig = new BlobTrackConfig();
    EvaluatorMultiRun test = new EvaluatorMultiRun(pipelineConfig);
    test.multiRun();
    test.summarizeResults();
  }
}
