// code by jph - adapted by mg
package ch.ethz.idsc.demo.mg.gui;

import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;

// this is very similar to DavisDetailModule and should provide a windows to see the pipeline computations
public class PipeDetailModule extends AbstractModule {
  private PipeViewer pipeViewer = //
      new PipeViewer(GokartLcmChannel.DAVIS_OVERVIEW);
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());

  @Override // from AbstractModule
  protected void first() throws Exception {
    windowConfiguration.attach(getClass(), pipeViewer.pipeViewerFrame.jFrame);
    pipeViewer.pipeViewerFrame.jFrame.setVisible(true);
    pipeViewer.start();
  }

  @Override // from AbstractModule
  protected void last() {
    pipeViewer.stop();
  }

  /***************************************************/
  public static void standalone() throws Exception {
    PipeDetailModule pipelineDetailModule = new PipeDetailModule();
    pipelineDetailModule.first();
    pipelineDetailModule.pipeViewer.pipeViewerFrame // that's a bit much :-(
        .jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
