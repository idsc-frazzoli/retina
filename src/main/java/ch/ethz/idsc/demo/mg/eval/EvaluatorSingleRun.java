// code by mg
package ch.ethz.idsc.demo.mg.eval;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import ch.ethz.idsc.demo.mg.pipeline.ImageBlob;
import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.demo.mg.util.vis.VisPipelineUtil;

/** loads estimated features from a CSV file and provides functions to run an evaluation. A single evaluation instant
 * is compared in a TrackingEvaluatorInstant object. */
/* package */ class EvaluatorSingleRun {
  private final List<List<ImageBlob>> groundTruthFeatures;
  private final List<List<ImageBlob>> estimatedFeatures;
  private final EvaluatorInstant[] evaluatorInstants;
  private final String logFileName;
  private final File evaluationImagesFilePath;
  private final File handLabelFile;
  private final File estimatedLabelFile;
  private final int[] groundTruthTimeStamps;
  private final boolean saveEvaluationFrame;
  private final int numberOfFiles;
  private String estimatedLabelFileName;
  private float averageRecall;
  private float averagePrecision;
  private int currentLabelInstant = 0;

  EvaluatorSingleRun(PipelineConfig pipelineConfig) {
    logFileName = pipelineConfig.davisConfig.logFileName.toString();
    numberOfFiles = EvaluationFileLocations.images(logFileName).list().length;
    evaluationImagesFilePath = EvaluationFileLocations.evaluatedImages(logFileName);
    handLabelFile = EvaluationFileLocations.handlabels(pipelineConfig.handLabelFileName.toString());
    groundTruthTimeStamps = EvalUtil.getTimestampsFromImages(numberOfFiles, logFileName);
    groundTruthFeatures = EvalUtil.loadFromCSV(handLabelFile, groundTruthTimeStamps);
    estimatedLabelFileName = pipelineConfig.estimatedLabelFileName.toString();
    estimatedLabelFile = EvaluationFileLocations.estimatedlabels(estimatedLabelFileName);
    estimatedFeatures = EvalUtil.loadFromCSV(estimatedLabelFile, groundTruthTimeStamps);
    evaluatorInstants = new EvaluatorInstant[numberOfFiles];
    for (int i = 0; i < numberOfFiles; i++)
      evaluatorInstants[i] = new EvaluatorInstant(pipelineConfig, groundTruthFeatures.get(i), estimatedFeatures.get(i));
    saveEvaluationFrame = pipelineConfig.saveEvaluationFrame;
  }

  public void runEvaluation() {
    for (int i = 0; i < numberOfFiles; i++) {
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
      VisPipelineUtil.drawImageBlob(rawEventsFrame.createGraphics(), groundTruthFeatures.get(currentLabelInstant).get(i), Color.GREEN);
    }
    // overlay estimatedFeatures
    for (int i = 0; i < estimatedFeatures.get(currentLabelInstant).size(); i++) {
      VisPipelineUtil.drawImageBlob(rawEventsFrame.createGraphics(), estimatedFeatures.get(currentLabelInstant).get(i), Color.RED);
    }
    saveImage(rawEventsFrame);
  }

  // load hand-labeled image of the current evaluation instant
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
      ImageIO.write(bufferedImage, "png", new File(evaluationImagesFilePath, toBeSaved));
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.printf("Evaluation frame saved as %s\n", toBeSaved);
  }

  private void computePerformance() {
    for (int i = 0; i < numberOfFiles; i++) {
      averageRecall += evaluatorInstants[i].getRecall() / numberOfFiles;
      averagePrecision += evaluatorInstants[i].getPrecision() / numberOfFiles;
    }
  }

  public double[] getResults() {
    return new double[] { averageRecall, averagePrecision };
  }

  // standalone application
  public static void main(String[] args) {
    PipelineConfig pipelineConfig = new PipelineConfig();
    EvaluatorSingleRun test = new EvaluatorSingleRun(pipelineConfig);
    test.runEvaluation();
  }
}
