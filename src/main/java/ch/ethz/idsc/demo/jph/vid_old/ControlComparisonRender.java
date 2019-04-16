// code by jph
package ch.ethz.idsc.demo.jph.vid_old;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.sca.Round;

public class ControlComparisonRender extends AbstractFrameRender {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._063.cyclic().deriveWithAlpha(128);
  // ---
  private final Map<ControlType, List<TrackDriving>> map;

  public ControlComparisonRender(Map<ControlType, List<TrackDriving>> map) {
    this.map = map;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
    for (Entry<ControlType, List<TrackDriving>> entry : map.entrySet()) {
      ControlType controlType = entry.getKey();
      int ordinal = controlType.ordinal();
      graphics.setColor(COLOR_DATA_INDEXED.getColor(controlType.ordinal()));
      int line = 1;
      graphics.drawString(controlType.name().toLowerCase(), 350 * ordinal, line * 30);
      ++line;
      for (TrackDriving trackDriving : entry.getValue()) {
        Scalar scalar = trackDriving.tensor.Get(Math.min(render_index, trackDriving.tensor.length() - 1), 0);
        graphics.drawString(String.format("%6s[s]", scalar.map(Round._3).toString()), 350 * ordinal, line * 30);
        ++line;
      }
    }
  }
}
