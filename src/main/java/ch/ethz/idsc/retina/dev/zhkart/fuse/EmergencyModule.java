// code by jpg
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.dev.zhkart.PutProvider;
import ch.ethz.idsc.retina.sys.AbstractModule;

abstract class EmergencyModule<PE> extends AbstractModule implements PutProvider<PE> {
  @Override // from PutProvider
  public final ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }
}
