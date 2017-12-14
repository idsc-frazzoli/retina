// code by jph
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.sys.AbstractModule;

/** prevents acceleration if something is in the way
 * for instance when a person is entering or leaving the gokart */
public final class Vlp16ClearanceModule extends AbstractModule implements RimoPutProvider {
  @Override
  protected void first() throws Exception {
    // TODO Auto-generated method stub
  }

  @Override
  protected void last() {
    // TODO Auto-generated method stub
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    // TODO Auto-generated method stub
    return null;
  }
}
