// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.Tensor;

/** class is used to develop and test anti lock brake logic */
public class LaneKeepingCenterlineModule extends AbstractModule implements GokartPoseListener {
  public GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  public GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  public Optional<Tensor> optionalCurve = Optional.empty();

  @Override // from AbstractModule
  public void first() {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
  }

  @Override // from AbstractModule
  public void last() {
    gokartPoseLcmClient.stopSubscriptions();
  }
  
  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }

  public void setCurve(Optional<Tensor> curve) {
    if (curve.isPresent()) {
      optionalCurve = curve;
    } else {
      System.err.println("Curve missing");
      optionalCurve = Optional.empty();
    }
  }

  final Optional<Tensor> getCurve() {
    System.out.println("got curve");
    return optionalCurve;
  }

}
