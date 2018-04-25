// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.mg.HandLabelFileLocations;
import ch.ethz.idsc.demo.mg.gui.AccumulatedEventFrame;
import ch.ethz.idsc.demo.mg.gui.PhysicalBlobFrame;
import ch.ethz.idsc.demo.mg.gui.PipelineVisualization;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

// this module distributes the event stream to the visualization and control pipeline
public class InputSubModule implements OfflineLogListener, DavisDvsListener {
  // event filtering
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder(); // for event processing
  private final EventFiltering surface = new EventFiltering(); // for filtering of event stream
  // feature filtering
  private final ImageBlobSelector imageBlobSelector = new ImageBlobSelector(); // receive tracked cones from that module
  // blob tracking
  private final BlobTracking tracking = new BlobTracking(imageBlobSelector); // next module in pipeline
  // blob transformation
  private final ImageToWorldTransform transformer = new ImageToWorldTransform();
  // visualization
  private final PipelineVisualization viz = new PipelineVisualization(); // visualization GUI
  private final AccumulatedEventFrame[] eventFrames = new AccumulatedEventFrame[3]; // perception module visualization frames
  private final PhysicalBlobFrame[] physicalFrames = new PhysicalBlobFrame[3]; // control module visualization frames
  // performance evaluation
  private final File pathToHandlabelsFile = HandLabelFileLocations.labels("labeledFeatures.dat"); // ground truth file for tracking evaluator
  // private final TrackingEvaluator evaluator = new TrackingEvaluator(pathToHandlabelsFile, track);
  // log file configuration
  private final int maxDuration = 10000; // [ms]
  private final int backgroundActivityFilterTime = 200; // [us] the shorter the more is filtered
  private final int imageInterval = 50; // [ms] visualization interval
  private final int savingInterval = 1000; // [ms] image saving interval
  private final boolean useFilter = true;
  private String imagePrefix = "Test"; // image name structure: "%s_%04d_%d.png", imagePrefix, imageCount, timeStamp
  private File pathToImages = HandLabelFileLocations.images(); // path where images are saved
  // fields for testing
  private float eventCount = 0;
  private float filteredEventCount;
  private int lastImagingTimestamp;
  private int lastSavingTimestamp;
  private int begin, end;
  private long startTime, endTime;
  private boolean saveImages = false;

  public InputSubModule() {
    davisDvsDatagramDecoder.addDvsListener(this);
    eventFrames[0] = new AccumulatedEventFrame();
    eventFrames[1] = new AccumulatedEventFrame();
    eventFrames[2] = new AccumulatedEventFrame();
    physicalFrames[0] = new PhysicalBlobFrame();
    physicalFrames[1] = new PhysicalBlobFrame();
    physicalFrames[2] = new PhysicalBlobFrame();
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals("davis240c.overview.dvs")) {
      davisDvsDatagramDecoder.decode(byteBuffer);
    }
  }

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    // initialize timers
    if (eventCount == 0) {
      begin = davisDvsEvent.time;
      lastImagingTimestamp = davisDvsEvent.time;
      lastSavingTimestamp = davisDvsEvent.time;
      startTime = System.currentTimeMillis();
    }
    ++eventCount;
    // send raw event stream to visualization
    eventFrames[0].receiveEvent(davisDvsEvent);
    // send filtered events to visualization, tracker and evaluator
    if (surface.backgroundActivityFilter(davisDvsEvent, backgroundActivityFilterTime) && useFilter) {
      tracking.receiveNewEvent(davisDvsEvent);
      // evaluator.receiveEvent(davisDvsEvent);
      eventFrames[1].receiveEvent(davisDvsEvent);
      eventFrames[2].receiveEvent(davisDvsEvent);
      transformer.transformBlobs(imageBlobSelector.getTrackedBlobs());
      ++filteredEventCount;
      // the events are accumulated for the interval time and then displayed in a single frame
      if ((davisDvsEvent.time - lastImagingTimestamp) > imageInterval * 1000) {
        viz.setImage(eventFrames[0].getAccumulatedEvents(), 0);
        // active blobs color coded by featurefilter
        viz.setImage(eventFrames[1].trackOverlay(imageBlobSelector.getTrackedBlobs()), 1);
        // hidden blobs
        viz.setImage(eventFrames[2].trackOverlay(tracking.getBlobList(0)), 2);
        // physical location of raw features
        viz.setImage(physicalFrames[0].receiveBlobs(transformer.getPhysicalBlobs()), 3);
        if (saveImages && (davisDvsEvent.time - lastSavingTimestamp) > savingInterval * 1000) {
          try {
            viz.saveImage(pathToImages, imagePrefix, davisDvsEvent.time);
          } catch (IOException e) {
            e.printStackTrace();
          }
          lastSavingTimestamp = davisDvsEvent.time;
        }
        eventFrames[0].clearImage();
        eventFrames[1].clearImage();
        eventFrames[2].clearImage();
        lastImagingTimestamp = davisDvsEvent.time;
      }
    }
    if (davisDvsEvent.time - begin > maxDuration * 1000) {
      end = davisDvsEvent.time;
      endTime = System.currentTimeMillis();
      int diff = end - begin;
      System.out.println("Percentage hit by active blobs: " + tracking.hitthreshold / eventCount * 100);
      System.out.println("Elapsed time in the eventstream [ms]: " + diff / 1000 + " with " + eventCount + " events");
      long elapsedTime = endTime - startTime;
      System.out.println("Computation time: " + elapsedTime + "[ms]");
      System.out.format("%.2f%% of the events were processed after filtering.", getFilteredPercentage());
      System.exit(0);
    }
  }

  public float getFilteredPercentage() {
    return 100 * filteredEventCount / eventCount;
  }
}
