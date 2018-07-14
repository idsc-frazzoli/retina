// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.util.SlamFileUtil;
import ch.ethz.idsc.demo.mg.util.SlamMapUtil;
import ch.ethz.idsc.tensor.Tensor;

/** executes the mapping step of the SLAM algorithm */
class SlamMappingStep {
  private final MapProvider[] eventMaps;
  private final String imagePrefix;
  private final boolean localizationMode;
  private final double lookAheadDistance;
  private final double normalizationUpdateRate;
  private final int relevantParticles;
  private double lastNormalizationTimeStamp;

  SlamMappingStep(SlamConfig slamConfig) {
    eventMaps = new MapProvider[3];
    for (int i = 0; i < 3; i++)
      eventMaps[i] = new MapProvider(slamConfig);
    imagePrefix = slamConfig.davisConfig.logFileName;
    localizationMode = slamConfig.localizationMode;
    lookAheadDistance = slamConfig.lookAheadDistance.number().doubleValue();
    normalizationUpdateRate = slamConfig.normalizationUpdateRate.number().doubleValue();
    relevantParticles = slamConfig.relevantParticles.number().intValue();
  }

  public void initialize(double initTimeStamp) {
    lastNormalizationTimeStamp = initTimeStamp;
    if (localizationMode) {
      double[] mapArray = SlamFileUtil.loadFromCSV(SlamFileLocations.recordedMaps(imagePrefix));
      if (mapArray.length != eventMaps[0].getNumberOfCells())
        throw new RuntimeException("FATAL: bad size");
      eventMaps[0].setMapArray(mapArray);
    }
  }

  public void mappingStep(SlamParticle[] slamParticles, double[] eventGokartFrame, double currentTimeStamp) {
    if (eventGokartFrame[0] < lookAheadDistance) {
      if (!localizationMode) {
        SlamMapUtil.updateOccurrenceMap(slamParticles, eventMaps[0], eventGokartFrame, relevantParticles);
      }
    }
    // normalization map currently unused
    if ((currentTimeStamp - lastNormalizationTimeStamp) > normalizationUpdateRate) {
      // SlamMapUtil.updateNormalizationMap(gokartLidarPose.getPose(), lastExpectedPose, eventMaps[1], imageToGokartLookup, gokartToImageUtil, 240, 180,
      // lookAheadDistance);
      // lastExpectedPose = gokartLidarPose.getPose();
      // MapProvider.divide(eventMaps[0], eventMaps[1], eventMaps[2]);
      // SlamParticleUtil.printStatusInfo(slamParticles);
      lastNormalizationTimeStamp = currentTimeStamp;
    }
  }

  public void mappingStepWithLidar(Tensor gokartPose, double[] eventGokartFrame, double currentTimeStamp) {
    // just to make sure
    if (localizationMode)
      System.out.println("FATAL: when mapping with lidar pose, localization mode should be false");
    if (eventGokartFrame[0] < lookAheadDistance)
      SlamMapUtil.updateOccurrenceMapLidar(gokartPose, eventMaps[0], eventGokartFrame);
  }

  public MapProvider getMap(int mapID) {
    return eventMaps[mapID];
  }
}
