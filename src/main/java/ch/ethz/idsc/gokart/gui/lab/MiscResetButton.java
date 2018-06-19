// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.event.ActionEvent;

import ch.ethz.idsc.retina.dev.misc.MiscIgnitionProvider;

/* package */ class MiscResetButton extends AutoboxInitButton {
  public MiscResetButton() {
    super("Reset");
  }

  @Override // from ActionListener
  public void actionPerformed(ActionEvent actionEvent) {
    MiscIgnitionProvider.INSTANCE.schedule();
  }

  @Override // from AutoboxInitButton
  public boolean isEnabled() {
    return MiscIgnitionProvider.INSTANCE.isScheduleSuggested();
  }
}
