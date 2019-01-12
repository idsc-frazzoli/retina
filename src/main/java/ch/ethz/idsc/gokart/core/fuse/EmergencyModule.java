// code by jpg
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

/** suggested base class for emergency modules that issue commands to actuators */
public abstract class EmergencyModule<PE> extends AbstractModule implements PutProvider<PE> {
  @Override // from PutProvider
  public final ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }
}
