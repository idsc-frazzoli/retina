// code by jph
package ch.ethz.idsc.gokart.core.map;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmClient;
import ch.ethz.idsc.owl.math.region.BufferedImageRegion;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

/** free space module always runs in the background
 * 
 * other modules that require free space information subscribe
 * to the instance of {@link OccupancyMappingModule} to obtain
 * an eroded snapshot of the current obstacles. */
public class OccupancyMappingModule extends AbstractModule implements Runnable {
  private final List<AbstractModule> abstractModules = new CopyOnWriteArrayList<>();
  private final Thread thread = new Thread(this);
  // ---
  private final OccupancyMappingCore occupancyMappingCore;
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final Vlp16LcmClient vlp16LcmClient;
  private volatile boolean isLaunched = true;

  public OccupancyMappingModule() {
    this(OccupancyConfig.GLOBAL);
  }

  public OccupancyMappingModule(OccupancyConfig occupancyConfig) {
    occupancyMappingCore = new OccupancyMappingCore(occupancyConfig) {
      @Override
      public void action() {
        thread.interrupt();
      }
    };
    vlp16LcmClient = SensorsConfig.GLOBAL.vlp16LcmClient(occupancyMappingCore.vlp16Decoder);
  }

  @Override
  protected void first() {
    gokartPoseLcmClient.addListener(occupancyMappingCore);
    gokartPoseLcmClient.startSubscriptions();
    vlp16LcmClient.startSubscriptions();
    thread.start();
  }

  @Override
  protected void last() {
    isLaunched = false;
    thread.interrupt();
    vlp16LcmClient.stopSubscriptions();
    gokartPoseLcmClient.stopSubscriptions();
  }

  public void subscribe(AbstractModule abstractModule) {
    abstractModules.add(abstractModule);
  }

  public void unsubscribe(AbstractModule abstractModule) {
    abstractModules.remove(abstractModule);
  }

  public BufferedImageRegion erodedMap(int radius) {
    return occupancyMappingCore.erodedMap(radius);
  }

  @Override
  public void run() {
    while (isLaunched) {
      boolean process = occupancyMappingCore.process();
      if (!process)
        try {
          Thread.sleep(1_000);
        } catch (Exception exception) {
          // ---
        }
    }
  }
}
