// code by jph
package ch.ethz.idsc.gokart.gui.top;

import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.GokartStatusLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoPutLcmClient;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931ImuLcmClient;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class LocalViewLcmModule extends AbstractModule {
  private static final Tensor POSE = Tensors.fromString("{0[m],-3[m],0}").unmodifiable();
  private static final Tensor MINOR_ACC = Tensors.vector(0.8, -0.0, 0);
  private static final Tensor MINOR_VEL = Tensors.vector(0.8, -6.0, 0);
  private static final Tensor MINORRIGHT = Tensors.vector(0, -3.5, 0);
  static final Tensor MODEL2PIXEL = Tensors.fromString("{{0,-100,200},{-100,0,300},{0,0,1}}").unmodifiable();
  // ---
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final RimoPutLcmClient rimoPutLcmClient = new RimoPutLcmClient();
  private final LinmotGetLcmClient linmotGetLcmClient = new LinmotGetLcmClient();
  private final GokartStatusLcmClient gokartStatusLcmClient = new GokartStatusLcmClient();
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final Vmu931ImuLcmClient vmu931ImuLcmClient = new Vmu931ImuLcmClient();
  // ---
  private final TimerFrame timerFrame = new TimerFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());

  @Override // from AbstractModule
  protected void first() {
    timerFrame.geometricComponent.setModel2Pixel(MODEL2PIXEL);
    {
      GokartRender gokartRender = new GokartRender();
      rimoGetLcmClient.addListener(gokartRender.rimoGetListener);
      rimoGetLcmClient.addListener(gokartRender.gokartAngularSlip);
      rimoPutLcmClient.addListener(gokartRender.rimoPutListener);
      linmotGetLcmClient.addListener(gokartRender.linmotGetListener);
      gokartStatusLcmClient.addListener(gokartRender.gokartStatusListener);
      gokartRender.gokartPoseListener.getEvent(GokartPoseEvents.create(Tensors.fromString("{0[m], -3[m], 0}"), RealScalar.ONE));
      // gokartPoseLcmClient.addListener(gokartRender.gokartPoseListener);
      timerFrame.geometricComponent.addRenderInterface(gokartRender);
    }
    {
      VelocityIndicatorRender velocityIndicatorRender = new VelocityIndicatorRender(GokartPoseHelper.toUnitless(POSE));
      rimoGetLcmClient.addListener(velocityIndicatorRender);
      timerFrame.geometricComponent.addRenderInterface(velocityIndicatorRender);
    }
    {
      AccelerationRender accelerationRender = new AccelerationRender(100, //
          Se2Utils.toSE2Matrix(MINOR_ACC).dot(GroundSpeedRender.DIAGONAL));
      vmu931ImuLcmClient.addListener(accelerationRender);
      timerFrame.geometricComponent.addRenderInterface(accelerationRender);
    }
    final Tensor matrix = Se2Utils.toSE2Matrix(MINOR_VEL).dot(GroundSpeedRender.DIAGONAL);
    {
      GroundSpeedRender groundSpeedRender = new GroundSpeedRender(50, matrix);
      gokartPoseLcmClient.addListener(groundSpeedRender);
      timerFrame.geometricComponent.addRenderInterface(groundSpeedRender);
    }
    {
      MPCExpectationRender mpcExpectationRender = new MPCExpectationRender(MINOR_ACC);
      rimoGetLcmClient.addListener(mpcExpectationRender);
      timerFrame.geometricComponent.addRenderInterface(mpcExpectationRender);
    }
    {
      BrakeCalibrationRender brakeCalibrationRender = new BrakeCalibrationRender(MINORRIGHT);
      timerFrame.geometricComponent.addRenderInterface(brakeCalibrationRender);
    }
    {
      TachometerMustangDash tachometerMustangDash = new TachometerMustangDash(matrix);
      rimoGetLcmClient.addListener(tachometerMustangDash);
      timerFrame.geometricComponent.addRenderInterface(tachometerMustangDash);
    }
    // ---
    gokartPoseLcmClient.startSubscriptions();
    rimoGetLcmClient.startSubscriptions();
    rimoPutLcmClient.startSubscriptions();
    linmotGetLcmClient.startSubscriptions();
    gokartStatusLcmClient.startSubscriptions();
    vmu931ImuLcmClient.startSubscriptions();
    // ---
    windowConfiguration.attach(getClass(), timerFrame.jFrame);
    timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    timerFrame.jFrame.setVisible(true);
  }

  @Override // from AbstractModule
  protected void last() {
    gokartPoseLcmClient.stopSubscriptions();
    rimoGetLcmClient.stopSubscriptions();
    rimoPutLcmClient.stopSubscriptions();
    linmotGetLcmClient.stopSubscriptions();
    gokartStatusLcmClient.stopSubscriptions();
    vmu931ImuLcmClient.stopSubscriptions();
    // ---
    timerFrame.close();
  }

  public static void standalone() throws Exception {
    LocalViewLcmModule localViewLcmModule = new LocalViewLcmModule();
    localViewLcmModule.first();
    localViewLcmModule.timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
