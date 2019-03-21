// code by jph
package ch.ethz.idsc.gokart.lcm.mod;

import java.util.Objects;

import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmServers;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

/* package */ abstract class VelodyneLcmServerModule extends AbstractModule {
  private final VelodyneModel velodyneModel;
  private final String channel;
  // ---
  private StartAndStoppable startAndStoppable = null;

  public VelodyneLcmServerModule(VelodyneModel velodyneModel, String channel) {
    this.velodyneModel = velodyneModel;
    this.channel = channel;
  }

  @Override // from AbstractModule
  protected final void first() {
    startAndStoppable = VelodyneLcmServers.ray(velodyneModel, channel, VelodyneStatics.RAY_DEFAULT_PORT);
    startAndStoppable.start();
  }

  @Override // from AbstractModule
  protected final void last() {
    if (Objects.nonNull(startAndStoppable)) {
      startAndStoppable.stop();
      startAndStoppable = null;
    }
  }
}
