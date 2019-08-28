// code by jph
package ch.ethz.idsc.gokart.core.map;

import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.ren.EmptyRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** occupancy mapping module periodically obtains an eroded map from the
 * occupancy mapping module and visualizes the eroded map in a frame.
 * 
 * Hint:
 * {@link OccupancyMappingModule} has to be started before
 * launching OccupancyViewerModule */
public class OccupancyViewerModule extends AbstractClockedModule {
  private final OccupancyMappingModule occupancyMappingModule = //
      ModuleAuto.INSTANCE.getInstance(OccupancyMappingModule.class);
  private final TimerFrame timerFrame = new TimerFrame(200, TimeUnit.MILLISECONDS);
  // ---
  private RenderInterface renderInterface = EmptyRender.INSTANCE;

  public OccupancyViewerModule() {
    timerFrame.geometricComponent.setModel2Pixel(LocalizationConfig.GLOBAL.getPredefinedMap().getModel2Pixel());
    timerFrame.geometricComponent.addRenderInterfaceBackground(new RenderInterface() {
      @Override
      public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        renderInterface.render(geometricLayer, graphics);
      }
    });
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  @Override
  protected void first() {
    occupancyMappingModule.subscribe(this);
    timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    timerFrame.jFrame.setVisible(true);
  }

  @Override
  protected void last() {
    timerFrame.jFrame.setVisible(false);
    timerFrame.jFrame.dispose();
    occupancyMappingModule.unsubscribe(this);
  }

  @Override
  protected void runAlgo() {
    renderInterface = occupancyMappingModule.erodedMap(3); // TODO magic const
  }

  @Override
  protected Scalar getPeriod() {
    return Quantity.of(0.5, SI.SECOND);
  }

  /***************************************************/
  public static void standalone() throws Exception {
    ModuleAuto.INSTANCE.runOne(OccupancyMappingModule.class);
    ModuleAuto.INSTANCE.runOne(OccupancyViewerModule.class);
    OccupancyViewerModule occupancyViewerModule = ModuleAuto.INSTANCE.getInstance(OccupancyViewerModule.class);
    occupancyViewerModule.timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    occupancyViewerModule.timerFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        ModuleAuto.INSTANCE.endOne(OccupancyViewerModule.class);
        ModuleAuto.INSTANCE.endOne(OccupancyMappingModule.class);
      }
    });
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
