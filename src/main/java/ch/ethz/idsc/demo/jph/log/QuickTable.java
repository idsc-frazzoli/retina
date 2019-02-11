// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.channel.LabjackAdcChannel;
import ch.ethz.idsc.gokart.offline.tab.SingleChannelTable;
import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.HomeDirectory;

enum QuickTable {
  ;
  public static void main(String[] args) throws IOException {
    OfflineTableSupplier offlineTableSupplier = SingleChannelTable.of(LabjackAdcChannel.INSTANCE);
    File file = DatahakiLogFileLocator.file(GokartLogFile._20190211T120203_e3c6742e);
    OfflineLogPlayer.process(file, offlineTableSupplier);
    Tensor table = offlineTableSupplier.getTable();
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel("Gokart Labjack ADC readout");
    visualSet.setAxesLabelX("time [s]");
    visualSet.setAxesLabelY("voltage [V]");
    Tensor domain = table.get(Tensor.ALL, 0);
    visualSet.add(domain, table.get(Tensor.ALL, 1)).setLabel("boost");
    visualSet.add(domain, table.get(Tensor.ALL, 2)).setLabel("reverse");
    visualSet.add(domain, table.get(Tensor.ALL, 3)).setLabel("throttle");
    visualSet.add(domain, table.get(Tensor.ALL, 4)).setLabel("autonomous button");
    visualSet.add(domain, table.get(Tensor.ALL, 5)).setLabel("ADC5 (not used)");
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    ChartUtils.saveChartAsPNG(HomeDirectory.Pictures("gokart_adc.png"), jFreeChart, 1280, 720);
  }
}
