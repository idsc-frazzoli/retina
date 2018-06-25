// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import ch.ethz.idsc.demo.mg.eval.EvaluationFileLocations;
import ch.ethz.idsc.demo.mg.gui.PipelineVisualization;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

// provides a pipeline "wrapper" for analyzing logfiles with visualization
// TODO visualization is not yet cleanly separated from PipelineProvider
public class OfflinePipelineWrap implements OfflineLogListener {
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  private final PipelineProvider pipelineProvider;
  // visualization
  private final boolean visualizePipeline;
  private int visualizationInterval;
  private PipelineVisualization visualizer = null;
  private int lastImagingTimestamp;
  private final boolean calibrationAvailable;
  // image saving
  private int saveImagesConfig;
  private int savingInterval;
  private String imagePrefix;
  private File parentFilePath;
  private int imageCount = 0;
  private int lastSavingTimestamp;
  // summary
  private boolean isInitialized;
  private int firstTimestamp, lastTimestamp;
  private long startTime, endTime;

  OfflinePipelineWrap(PipelineConfig pipelineConfig) {
    pipelineProvider = new PipelineProvider(pipelineConfig);
    davisDvsDatagramDecoder.addDvsListener(pipelineProvider);
    visualizePipeline = pipelineConfig.visualizePipeline;
    visualizationInterval = pipelineConfig.visualizationInterval.number().intValue();
    if (visualizePipeline)
      visualizer = new PipelineVisualization();
    saveImagesConfig = pipelineConfig.saveImagesConfig.number().intValue();
    imagePrefix = pipelineConfig.logFileName.toString();
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
      if (visualizePipeline && (timeInst - lastImagingTimestamp) > visualizationInterval) {
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
    System.out.println("Percentage hit by active blobs: " + pipelineProvider.tracking.hitthreshold / pipelineProvider.eventCount * 100);
    System.out.println("Elapsed time in the eventstream [ms]: " + diff + " with " + pipelineProvider.eventCount + " events");
    long elapsedTime = endTime - startTime;
    System.out.println("Computation time: " + elapsedTime + "[ms]");
    System.out.format("%.2f%% of the events were processed after filtering.\n", (100 * pipelineProvider.filteredEventCount / pipelineProvider.eventCount));
  }

  // for image saving
  private void saveFrame(File parentFilePath, String imagePrefix, int timeStamp) {
    try {
      imageCount++;
      String fileName = String.format("%s_%04d_%d.png", imagePrefix, imageCount, timeStamp);
      String secondFileName = String.format("%s_%s_%04d_%d.png", "physical", imagePrefix, imageCount, timeStamp);
      ImageIO.write(pipelineProvider.eventFrames[1].getAccumulatedEvents(), "png", new File(parentFilePath, fileName));
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
    for (int i = 0; i < pipelineProvider.eventFrames.length; i++) {
      pipelineProvider.eventFrames[i].clearImage();
    }
  }

  // for visualization
  private BufferedImage[] constructFrames() {
    BufferedImage[] combinedFrames = new BufferedImage[6];
    combinedFrames[0] = pipelineProvider.eventFrames[0].getAccumulatedEvents();
    combinedFrames[1] = pipelineProvider.eventFrames[1].overlayActiveBlobs(pipelineProvider.blobSelector.getProcessedBlobs(), Color.GREEN, Color.RED);
    combinedFrames[2] = pipelineProvider.eventFrames[2].overlayHiddenBlobs(pipelineProvider.tracking.getHiddenBlobs(), Color.GRAY);
    if (calibrationAvailable) {
      combinedFrames[3] = pipelineProvider.physicalFrames[0].overlayPhysicalBlobs((pipelineProvider.getProcessedblobs()));
      // currently unused
      combinedFrames[4] = pipelineProvider.physicalFrames[1].getFrame();
      combinedFrames[5] = pipelineProvider.physicalFrames[2].getFrame();
    }
    return combinedFrames;
  }
}
