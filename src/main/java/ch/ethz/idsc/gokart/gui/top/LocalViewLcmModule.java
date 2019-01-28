// code by jph
package ch.ethz.idsc.gokart.gui.top;

import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.ekf.SimpleVelocityEstimation;
import ch.ethz.idsc.gokart.lcm.autobox.GokartStatusLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoPutLcmClient;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931ImuLcmClient;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class LocalViewLcmModule extends AbstractModule {
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  private static final Tensor POSE = Tensors.fromString("{0[m],0[m],0}").unmodifiable();
  private static final Tensor MINOR = Tensors.vector(0, -2.5, 0);
  static final Tensor MODEL2PIXEL = Tensors.fromString("{{0,-100,200},{-100,0,300},{0,0,1}}").unmodifiable();
  // ---
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final RimoPutLcmClient rimoPutLcmClient = new RimoPutLcmClient();
  private final LinmotGetLcmClient linmotGetLcmClient = new LinmotGetLcmClient();
  private final GokartStatusLcmClient gokartStatusLcmClient = new GokartStatusLcmClient();
  private final Vmu931ImuLcmClient vmu931ImuLcmClient = new Vmu931ImuLcmClient();
  private final TimerFrame timerFrame = new TimerFrame();
  private final AccelerationRender accelerationRender = new AccelerationRender(MINOR, 100);
  private final GroundSpeedRender groundSpeedRender = new GroundSpeedRender(MINOR);
  private final GokartRender gokartRender = new GokartRender(() -> POSE, VEHICLE_MODEL);
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());

  @Override
  protected void first() throws Exception {
    ModuleAuto.INSTANCE.runOne(SimpleVelocityEstimation.class);
    timerFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
    rimoGetLcmClient.addListener(gokartRender.rimoGetListener);
    rimoPutLcmClient.addListener(gokartRender.rimoPutListener);
    linmotGetLcmClient.addListener(gokartRender.linmotGetListener);
    gokartStatusLcmClient.addListener(gokartRender.gokartStatusListener);
    rimoGetLcmClient.addListener(gokartRender.gokartAngularSlip);
    vmu931ImuLcmClient.addListener(vmu931ImuFrame -> accelerationRender.setAccelerationXY(vmu931ImuFrame.accXY()));
    // ---
    timerFrame.geometricComponent.setModel2Pixel(MODEL2PIXEL);
    timerFrame.geometricComponent.addRenderInterface(gokartRender);
    timerFrame.geometricComponent.addRenderInterface(accelerationRender);
    timerFrame.geometricComponent.addRenderInterface(gokartRender);
    timerFrame.geometricComponent.addRenderInterface(groundSpeedRender);
    TachometerMustangDash tachometerMustangDash = new TachometerMustangDash(Tensors.vector(1, -2.5, 0));
    rimoGetLcmClient.addListener(tachometerMustangDash);
    timerFrame.geometricComponent.addRenderInterface(tachometerMustangDash);
    // ---
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

  @Override
  protected void last() {
    ModuleAuto.INSTANCE.endOne(SimpleVelocityEstimation.class);
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
