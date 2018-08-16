// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

import javax.imageio.ImageIO;

import ch.ethz.idsc.demo.mg.eval.EvaluationFileLocations;
import ch.ethz.idsc.demo.mg.gui.PipelineVisualization;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

// provides a pipeline "wrapper" for analyzing logfiles with visualization
// TODO visualization is not yet cleanly separated from PipelineProvider
class OfflinePipelineWrap implements OfflineLogListener {
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  private final PipelineProvider pipelineProvider;
  // visualization
  // private final boolean visualizePipeline;
  private final int visualizationInterval;
  private final PipelineVisualization visualizer;
  private int lastImagingTimestamp;
  private final boolean calibrationAvailable;
  // image saving
  private final int saveImagesConfig;
  private final int savingInterval;
  private final String imagePrefix;
  private final File parentFilePath;
  private int imageCount = 0;
  private int lastSavingTimestamp;
  // summary
  private boolean isInitialized;
  private int firstTimestamp, lastTimestamp;
  private long startTime, endTime;

  OfflinePipelineWrap(PipelineConfig pipelineConfig) {
    pipelineProvider = new PipelineProvider(pipelineConfig);
    davisDvsDatagramDecoder.addDvsListener(pipelineProvider);
    visualizationInterval = pipelineConfig.visualizationInterval.number().intValue();
    visualizer = pipelineConfig.visualizePipeline ? new PipelineVisualization() : null;
    saveImagesConfig = pipelineConfig.saveImagesConfig.number().intValue();
    imagePrefix = pipelineConfig.davisConfig.logFilename();
    if (saveImagesConfig == 1) {
      parentFilePath = EvaluationFileLocations.testing();
    } else {
      parentFilePath = EvaluationFileLocations.images(imagePrefix);
    }
    savingInterval = pipelineConfig.savingInterval.number().intValue();
    calibrationAvailable = pipelineConfig.calibrationAvailable;
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals("davis240c.overview.dvs")) {
      davisDvsDatagramDecoder.decode(byteBuffer);
      int timeInst = (int) (1000 * time.number().doubleValue()); // TODO hack
      // initialize timers
      if (!isInitialized) {
        startTime = System.currentTimeMillis();
        firstTimestamp = timeInst;
        lastImagingTimestamp = timeInst;
        lastSavingTimestamp = timeInst;
        isInitialized = true;
      }
      // the events are accumulated for the interval time and then displayed in a single frame
      if (Objects.nonNull(visualizer) && (timeInst - lastImagingTimestamp) > visualizationInterval) {
        // visualization repaint
        visualizer.setFrames(constructFrames());
        resetAllFrames();
        lastImagingTimestamp = timeInst;
      }
      // save frames
      if ((saveImagesConfig != 0) && (timeInst - lastSavingTimestamp) > savingInterval) {
        saveFrame(parentFilePath, imagePrefix, timeInst);
        lastSavingTimestamp = timeInst;
      }
      lastTimestamp = timeInst;
    }
  }

  public void summarizeLog() {
    endTime = System.currentTimeMillis();
    int diff = lastTimestamp - firstTimestamp;
    long elapsedTime = endTime - startTime;
    System.out.println("Computation time: " + elapsedTime + "[ms]");
    System.out.format("%.2f%% of the events were processed after filtering.\n", pipelineProvider.getEventFiltering().getFilteredPercentage());
  }

  // for image saving
  private void saveFrame(File parentFilePath, String imagePrefix, int timeStamp) {
    try {
      imageCount++;
      String fileName = String.format("%s_%04d_%d.png", imagePrefix, imageCount, timeStamp);
      String secondFileName = String.format("%s_%s_%04d_%d.png", "physical", imagePrefix, imageCount, timeStamp);
      ImageIO.write(pipelineProvider.getEventFrames()[1].getAccumulatedEvents(), "png", new File(parentFilePath, fileName));
      // ImageIO.write(physicalFrames[0].overlayPhysicalBlobs((transformer.getPhysicalBlobs())), "png", new File(parentFilePath, secondFileName));
      // possibility to save whole GUI
      // BufferedImage wholeGUI = viz.getGUIFrame();
      // ImageIO.write(wholeGUI, "png", new File(parentFilePath, fileName));
      System.out.printf("Images saved as %s\n", fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // for visualization
  private void resetAllFrames() {
    for (int i = 0; i < pipelineProvider.getEventFrames().length; i++) {
      pipelineProvider.getEventFrames()[i].clearImage();
    }
  }

  // for visualization
  private BufferedImage[] constructFrames() {
    BufferedImage[] combinedFrames = new BufferedImage[6];
    combinedFrames[0] = pipelineProvider.getEventFrames()[0].getAccumulatedEvents();
    combinedFrames[1] = pipelineProvider.getEventFrames()[1].overlayActiveBlobs(pipelineProvider.getBlobSelector().getProcessedBlobs(), Color.GREEN, Color.RED);
    combinedFrames[2] = pipelineProvider.getEventFrames()[2].overlayHiddenBlobs(pipelineProvider.getBlobTracking().getHiddenBlobs(), Color.GRAY);
    if (calibrationAvailable) {
      combinedFrames[3] = pipelineProvider.getPhysicalFrames()[0].overlayPhysicalBlobs((pipelineProvider.getProcessedblobs()));
      // currently unused
      combinedFrames[4] = pipelineProvider.getPhysicalFrames()[1].getFrame();
      combinedFrames[5] = pipelineProvider.getPhysicalFrames()[2].getFrame();
    }
    return combinedFrames;
  }
}
