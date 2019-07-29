// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.RimoTireConfiguration;
import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoRateControllerUno;
import ch.ethz.idsc.gokart.dev.rimo.RimoRateControllerWrap;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

/** class is used to reduce the velocity as a safety measurement */
public class MeasurementSlowDownModule extends AbstractModule implements PutProvider<RimoPutEvent> {
  final RimoRateControllerWrap rimoRateControllerWrap = new RimoRateControllerUno();
  private RimoGetEvent rimoGetEvent = RimoGetEvents.motionless();
  final RimoGetListener rimoGetListener = new RimoGetListener() {
    @Override
    public void getEvent(RimoGetEvent getEvent) {
      rimoGetEvent = getEvent;
      rimoRateControllerWrap.getEvent(getEvent);
    }
  };

  @Override // from AbstractModule
  protected void first() {
    RimoSocket.INSTANCE.addPutProvider(this);
    RimoSocket.INSTANCE.addGetListener(rimoGetListener);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    RimoSocket.INSTANCE.removeGetListener(rimoGetListener);
  }

  @Override // from LinmotPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    Tensor currAngularRate = rimoGetEvent.getAngularRate_Y_pair();
    Scalar maxVel = Quantity.of(2, SI.VELOCITY);
    Scalar maxAngularRate;
    maxAngularRate = maxVel.divide(RimoTireConfiguration._REAR.radius());
    // Angular velocity is limited by MaxAngularRate
    if (Scalars.lessThan(maxAngularRate, currAngularRate.Get(0)) || Scalars.lessThan(maxAngularRate, currAngularRate.Get(1))) {
      System.out.println("maxRate:" + " " + maxAngularRate.map(Round._3) + " " //
          + "currRate:" + " " + rimoGetEvent.getAngularRate_Y_pair().map(Round._3));
      return rimoRateControllerWrap.iterate(maxAngularRate);
    }
    return Optional.empty();
  }
}
