// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.gokart.offline.channel.GokartStatusChannel;
import ch.ethz.idsc.gokart.offline.channel.RimoGetChannel;
import ch.ethz.idsc.gokart.offline.channel.RimoPutChannel;
import ch.ethz.idsc.gokart.offline.channel.SingleChannelInterface;
import ch.ethz.idsc.gokart.offline.channel.Vmu931ImuChannel;
import ch.ethz.idsc.subare.util.HtmlUtf8;
import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.Import;

public class LogReport {
  private static final int WIDTH = 854;
  private static final int HEIGHT = 480;
  private final File plot;
  private final Map<SingleChannelInterface, Tensor> map;

  public LogReport(File directory) {
    plot = new File(directory, "plot");
    plot.mkdir();
    map = DynamicsConversion.SINGLE_CHANNEL_INTERFACES.stream() //
        .collect(Collectors.toMap(Function.identity(), singleChannelInterface -> {
          try {
            return Import.of(new File(directory, singleChannelInterface.channel() + ".csv.gz"));
          } catch (Exception exception) {
            throw new RuntimeException();
          }
        }));
    try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(directory, "index.html"))) {
      htmlUtf8.appendln("<h2>Steering</h2>");
      htmlUtf8.appendln("<img src='plot/status.png' /><br/><hr/>");
      htmlUtf8.appendln("<img src='plot/steerget.png' /><br/><hr/>");
      htmlUtf8.appendln("<h2>Motors</h2>");
      htmlUtf8.appendln("<img src='plot/rimoput.png' /><br/><hr/>");
      htmlUtf8.appendln("<img src='plot/rimoget.png' /><br/><hr/>");
      htmlUtf8.appendln("<h2>VMU931 IMU</h2>");
      htmlUtf8.appendln("<img src='plot/vmu931acc.png' /><br/><hr/>");
    }
  }

  public void exportStatus() throws IOException {
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel("Steering position (0 = straight)");
    visualSet.setAxesLabelX("time [s]");
    visualSet.setAxesLabelY("Steering position [n.a.]");
    Tensor tensor = map.get(GokartStatusChannel.INSTANCE);
    Tensor domain = tensor.get(Tensor.ALL, 0);
    visualSet.add(domain, tensor.get(Tensor.ALL, 1));
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    ChartUtils.saveChartAsPNG(new File(plot, "status.png"), jFreeChart, WIDTH, HEIGHT);
  }

  public void exportRimoPut() throws IOException {
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel("Rimo Motors torque command");
    visualSet.setAxesLabelX("time [s]");
    visualSet.setAxesLabelY("torque command [ARMS]");
    Tensor tensor = map.get(RimoPutChannel.INSTANCE);
    Tensor domain = tensor.get(Tensor.ALL, 0);
    visualSet.add(domain, tensor.get(Tensor.ALL, 1)).setLabel("left");
    visualSet.add(domain, tensor.get(Tensor.ALL, 2)).setLabel("right");
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    ChartUtils.saveChartAsPNG(new File(plot, "rimoput.png"), jFreeChart, WIDTH, HEIGHT);
  }

  public void exportRimoGet() throws IOException {
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel("Rimo Motors readings");
    visualSet.setAxesLabelX("time [s]");
    visualSet.setAxesLabelY("wheel rate [rad*s^-1]");
    Tensor tensor = map.get(RimoGetChannel.INSTANCE);
    Tensor domain = tensor.get(Tensor.ALL, 0);
    visualSet.add(domain, tensor.get(Tensor.ALL, 2)).setLabel("left");
    visualSet.add(domain, tensor.get(Tensor.ALL, 2 + 7)).setLabel("right");
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    ChartUtils.saveChartAsPNG(new File(plot, "rimoget.png"), jFreeChart, WIDTH, HEIGHT);
  }

  public void exportVmu931() throws IOException {
    VisualSet visualSet = new VisualSet(ColorDataLists._097.cyclic().deriveWithAlpha(64));
    visualSet.setPlotLabel("VMU931 acceleration");
    visualSet.setAxesLabelX("time [s]");
    visualSet.setAxesLabelY("acceleration [m*s^-2]");
    Tensor tensor = map.get(Vmu931ImuChannel.INSTANCE);
    Tensor domain = tensor.get(Tensor.ALL, 0);
    visualSet.add(domain, tensor.get(Tensor.ALL, 2)).setLabel("x");
    visualSet.add(domain, tensor.get(Tensor.ALL, 3)).setLabel("y");
    visualSet.add(domain, tensor.get(Tensor.ALL, 4)).setLabel("z");
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    ChartUtils.saveChartAsPNG(new File(plot, "vmu931acc.png"), jFreeChart, WIDTH, HEIGHT);
  }

  public static void main(String[] args) throws IOException {
    File directory = new File("/media/datahaki/data/gokart/dynamics/20190208T145312_03");
    LogReport logReport = new LogReport(directory);
    logReport.exportStatus();
    logReport.exportRimoPut();
    logReport.exportRimoGet();
    logReport.exportVmu931();
  }
}
