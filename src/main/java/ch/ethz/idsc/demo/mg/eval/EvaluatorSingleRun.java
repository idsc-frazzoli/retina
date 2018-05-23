// code by mg
package ch.ethz.idsc.demo.mg.eval;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import ch.ethz.idsc.demo.mg.pipeline.ImageBlob;
import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.demo.mg.util.CSVUtil;
import ch.ethz.idsc.demo.mg.util.VisualizationUtil;

/** loads estimated features from a CSV file and provides functions to run an evaluation. A single evaluation instant
 * is compared in a TrackingEvaluatorInstant object. */
public class EvaluatorSingleRun {
  private final List<List<ImageBlob>> groundTruthFeatures;
  private final List<List<ImageBlob>> estimatedFeatures;
  private final EvaluatorInstant[] evaluatorInstants;
  private final String logFileName;
  private final File evaluationFilePath;
  private final File handLabelFile;
  private final File estimatedLabelFile;
  private final int[] groundTruthTimeStamps;
  private final int[] estimatedTimeStamps;
  private final boolean saveEvaluationFrame;
  private final int numberOfLabelInstants;
  private String estimatedLabelFileName;
  private float averageRecall;
  private float averagePrecision;
  private int currentLabelInstant = 0;

  EvaluatorSingleRun(PipelineConfig pipelineConfig) {
    logFileName = pipelineConfig.logFileName.toString();
    evaluationFilePath = EvaluationFileLocations.evaluatedImages(logFileName);
    handLabelFile = EvaluationFileLocations.handlabels(pipelineConfig.handLabelFileName.toString());
    groundTruthTimeStamps = CSVUtil.getTimestampsFromCSV(handLabelFile);
    groundTruthFeatures = CSVUtil.loadFromCSV(handLabelFile);
    numberOfLabelInstants = groundTruthFeatures.size();
    estimatedLabelFileName = pipelineConfig.estimatedLabelFileName.toString();
    estimatedLabelFile = EvaluationFileLocations.estimatedlabels(estimatedLabelFileName);
    estimatedTimeStamps = CSVUtil.getTimestampsFromCSV(estimatedLabelFile);
    estimatedFeatures = CSVUtil.loadFromCSV(estimatedLabelFile);
    evaluatorInstants = new EvaluatorInstant[numberOfLabelInstants];
    saveEvaluationFrame = pipelineConfig.saveEvaluationFrame;
    // initialize evaluatorInstants
    for (int i = 0; i < numberOfLabelInstants; i++) {
      // if groundTruthtimeStamp[i] is not available in estimatedTimeStamps, we insert an empty list in the estimatedFeatures at that index
      if (Arrays.binarySearch(estimatedTimeStamps, groundTruthTimeStamps[i]) < 0) {
        List<ImageBlob> emptyList = new ArrayList<>();
        estimatedFeatures.add(i, emptyList);
      }
      evaluatorInstants[i] = new EvaluatorInstant(pipelineConfig, groundTruthFeatures.get(i), estimatedFeatures.get(i));
    }
  }

  public void runEvaluation() {
    for (int i = 0; i < numberOfLabelInstants; i++) {
      currentLabelInstant = i;
      evaluatorInstants[i].compareFeatures();
      if (saveEvaluationFrame) {
        saveEvaluationFrame();
      }
    }
    computePerformance();
  }

  // accumulatedEventFrame with estimated and ground truth features
  private void saveEvaluationFrame() {
    BufferedImage rawEventsFrame = loadImage();
    // overlay groundtruthFeatures
    for (int i = 0; i < groundTruthFeatures.get(currentLabelInstant).size(); i++) {
      VisualizationUtil.drawImageBlob(rawEventsFrame.createGraphics(), groundTruthFeatures.get(currentLabelInstant).get(i), Color.GREEN);
    }
    // overlay estimatedFeatures
    for (int i = 0; i < estimatedFeatures.get(currentLabelInstant).size(); i++) {
      VisualizationUtil.drawImageBlob(rawEventsFrame.createGraphics(), estimatedFeatures.get(currentLabelInstant).get(i), Color.RED);
    }
    saveImage(rawEventsFrame);
  }

  // load handlabelImage of the current evaluation instant
  private BufferedImage loadImage() {
    BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
    // load matching accumulatedEventFrame (very similar as in HandLabeler)
    String imgNumberString = String.format("%04d", currentLabelInstant + 1);
    String fileName = logFileName + "_" + imgNumberString + "_" + groundTruthTimeStamps[currentLabelInstant] + ".png";
    File pathToFile = new File(EvaluationFileLocations.images(logFileName), fileName);
    try {
      bufferedImage = ImageIO.read(pathToFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bufferedImage;
  }

  private void saveImage(BufferedImage bufferedImage) {
    String toBeSaved = String.format("%s_%04d_%d_%s.png", logFileName, currentLabelInstant + 1, groundTruthTimeStamps[currentLabelInstant], "evaluated");
    try {
      ImageIO.write(bufferedImage, "png", new File(evaluationFilePath, toBeSaved));
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.printf("Evaluation frame saved as %s\n", toBeSaved);
  }

  private void computePerformance() {
    for (int i = 0; i < numberOfLabelInstants; i++) {
      averageRecall += evaluatorInstants[i].getRecall() / numberOfLabelInstants;
      averagePrecision += evaluatorInstants[i].getPrecision() / numberOfLabelInstants;
    }
  }

  // TODO provide all essential results of the evaluation run to other objects through this fct
  public double[] getResults() {
    double[] results = new double[2];
    results[0] = averageRecall;
    results[1] = averagePrecision;
    return results;
  }

  // standalone application
  public static void main(String[] args) {
    PipelineConfig pipelineConfig = new PipelineConfig();
    EvaluatorSingleRun test = new EvaluatorSingleRun(pipelineConfig);
    test.runEvaluation();
  }
}
