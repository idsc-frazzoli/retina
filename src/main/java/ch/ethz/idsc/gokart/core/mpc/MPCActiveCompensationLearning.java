package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931ImuLcmClient;
import ch.ethz.idsc.owl.data.IntervalClock;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.filter.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MPCActiveCompensationLearning extends MPCControlUpdateListenerWithAction implements StartAndStoppable, RimoGetListener {
  private final static MPCActiveCompensationLearning INSTANCE = new MPCActiveCompensationLearning();
  public static MPCActiveCompensationLearning getInstance() {
    return INSTANCE;
  }
  private boolean running = false;
  private ControlAndPredictionSteps lastCNS = null;
  IntervalClock updateClock = new IntervalClock();
  IntervalClock rimoClock = new IntervalClock();
  private Scalar lastTangentialSpeed = Quantity.of(0, SI.VELOCITY);
  private final GeodesicIIR1Filter accelerationFilter = //
      new GeodesicIIR1Filter(RnGeodesic.INSTANCE, RealScalar.of(.02));
  private Scalar rimoAcceleration = Quantity.of(0, SI.ACCELERATION);
  private final Vmu931ImuLcmClient vmu931ImuLcmClient = new Vmu931ImuLcmClient();
  
  Scalar steeringCorrection = RealScalar.ONE;
  Scalar brakingCorrection = RealScalar.ONE;
  @Override
  void doAction() {
    double seconds = updateClock.seconds();
    if(Objects.nonNull(lastCNS)&&running)
    {
       Scalar wantedAcceleration = lastCNS.steps[0].control.getaB();
       //Scalar currentSteeringAngle 
    }
    lastCNS= cns;
  }
  @Override
  public void start() {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void stop() {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void getEvent(RimoGetEvent getEvent) {
    Scalar currentTangentSpeed = ChassisGeometry.GLOBAL.odometryTangentSpeed(getEvent);
    Scalar acceleration = currentTangentSpeed//
        .subtract(lastTangentialSpeed)//
        .divide(Quantity.of(rimoClock.seconds(), SI.SECOND));
    rimoAcceleration = (Scalar) accelerationFilter.apply(acceleration);
  }
  
}
