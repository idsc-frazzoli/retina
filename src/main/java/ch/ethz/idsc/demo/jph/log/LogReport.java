// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.awt.BasicStroke;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.channel.GokartStatusChannel;
import ch.ethz.idsc.gokart.offline.channel.LinmotGetVehicleChannel;
import ch.ethz.idsc.gokart.offline.channel.LinmotPutVehicleChannel;
import ch.ethz.idsc.gokart.offline.channel.RimoGetChannel;
import ch.ethz.idsc.gokart.offline.channel.RimoPutChannel;
import ch.ethz.idsc.gokart.offline.channel.SingleChannelInterface;
import ch.ethz.idsc.gokart.offline.channel.SteerGetChannel;
import ch.ethz.idsc.gokart.offline.channel.SteerPutChannel;
import ch.ethz.idsc.gokart.offline.channel.Vmu931ImuVehicleChannel;
import ch.ethz.idsc.sophus.group.LieDifferences;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2Group;
import ch.ethz.idsc.sophus.math.WindowCenterSampler;
import ch.ethz.idsc.subare.util.HtmlUtf8;
import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualRow;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ListConvolve;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.sca.win.GaussianWindow;

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
            return Import.of(new File(directory, singleChannelInterface.exportName() + ".csv.gz"));
          } catch (Exception exception) {
            throw new RuntimeException();
          }
        }));
    try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(directory, "index.html"))) {
      htmlUtf8.appendln("<h1>" + directory.getName() + "</h1>");
      htmlUtf8.appendln("<p><small>report generated: " + new Date() + "</small>");
      htmlUtf8.appendln("<h2>Steering</h2>");
      htmlUtf8.appendln("<img src='plot/status.png' /><br/><br/>");
      htmlUtf8.appendln("<img src='plot/steerget.png' /><br/><br/>");
      htmlUtf8.appendln("<h2>Rear Wheel Motors</h2>");
      htmlUtf8.appendln("<img src='plot/rimoput.png' /><br/><br/>");
      htmlUtf8.appendln("<img src='plot/rimoget.png' /><br/><br/>");
      htmlUtf8.appendln("<h2>Brake</h2>");
      htmlUtf8.appendln("<img src='plot/linmotPosition.png' /><br/><br/>");
      htmlUtf8.appendln("<img src='plot/linmotTemperature.png' /><br/><br/>");
      htmlUtf8.appendln("<h2>VMU931 IMU</h2>");
      htmlUtf8.appendln("<img src='plot/vmu931acc.png' /><br/><br/>");
      htmlUtf8.appendln("<img src='plot/vmu931gyro.png' /><br/><br/>");
      htmlUtf8.appendln("<h2>Pose</h2>");
      htmlUtf8.appendln("<img src='plot/pose_raw.png' /><br/><br/>");
      htmlUtf8.appendln("<img src='plot/pose_smooth.png' /><br/><br/>");
      htmlUtf8.appendln("<img src='plot/speeds.png' /><br/><br/>");
      htmlUtf8.appendln("<img src='plot/vmu931accSmooth.png' /><br/><br/>");
    }
  }

  public void exportStatus() throws IOException {
    VisualSet visualSet = new VisualSet(ColorDataLists._097.cyclic().deriveWithAlpha(128));
    visualSet.setPlotLabel("power steering position");
    visualSet.setAxesLabelX("time [s]");
    visualSet.setAxesLabelY("power steering position [n.a.]");
    {
      Tensor tensor = map.get(SteerGetChannel.INSTANCE);
      Tensor domain = tensor.get(Tensor.ALL, 0);
      visualSet.add(domain, tensor.get(Tensor.ALL, 8)).setLabel("raw");
    }
    {
      Tensor tensor = map.get(GokartStatusChannel.INSTANCE);
      Tensor domain = tensor.get(Tensor.ALL, 0);
      visualSet.add(domain, tensor.get(Tensor.ALL, 1)).setLabel("calibrated (0 = straight)");
    }
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    ChartUtils.saveChartAsPNG(new File(plot, "status.png"), jFreeChart, WIDTH, HEIGHT);
  }

  public void exportSteerGet() throws IOException {
    VisualSet visualSet = new VisualSet(ColorDataLists._097.cyclic().deriveWithAlpha(128));
    visualSet.setPlotLabel("power steering torque");
    visualSet.setAxesLabelX("time [s]");
    visualSet.setAxesLabelY("torque [n.a.]");
    {
      Tensor tensor = map.get(SteerPutChannel.INSTANCE);
      Tensor domain = tensor.get(Tensor.ALL, 0);
      visualSet.add(domain, tensor.get(Tensor.ALL, 2)).setLabel("commanded");
    }
    {
      Tensor tensor = map.get(SteerGetChannel.INSTANCE);
      Tensor domain = tensor.get(Tensor.ALL, 0);
      visualSet.add(domain, tensor.get(Tensor.ALL, 5)).setLabel("effective");
    }
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    ChartUtils.saveChartAsPNG(new File(plot, "steerget.png"), jFreeChart, WIDTH, HEIGHT);
  }

  public void exportRimoPut() throws IOException {
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel("rear wheel motors torque command");
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
    visualSet.setPlotLabel("rear wheel motors rotational rate");
    visualSet.setAxesLabelX("time [s]");
    visualSet.setAxesLabelY("wheel rotational rate [rad*s^-1]");
    Tensor tensor = map.get(RimoGetChannel.INSTANCE);
    Tensor domain = tensor.get(Tensor.ALL, 0);
    visualSet.add(domain, tensor.get(Tensor.ALL, 2)).setLabel("left");
    visualSet.add(domain, tensor.get(Tensor.ALL, 2 + 7)).setLabel("right");
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    ChartUtils.saveChartAsPNG(new File(plot, "rimoget.png"), jFreeChart, WIDTH, HEIGHT);
  }

  public void exportLinmotPosition() throws IOException {
    VisualSet visualSet = new VisualSet(ColorDataLists._097.cyclic().deriveWithAlpha(192));
    visualSet.setPlotLabel("Brake position");
    visualSet.setAxesLabelX("time [s]");
    visualSet.setAxesLabelY("brake position [m]");
    {
      Tensor tensor = map.get(LinmotPutVehicleChannel.INSTANCE);
      Tensor domain = tensor.get(Tensor.ALL, 0);
      visualSet.add(domain, tensor.get(Tensor.ALL, 1)).setLabel("command");
    }
    {
      Tensor tensor = map.get(LinmotGetVehicleChannel.INSTANCE);
      Tensor domain = tensor.get(Tensor.ALL, 0);
      visualSet.add(domain, tensor.get(Tensor.ALL, 1)).setLabel("effective");
    }
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    ChartUtils.saveChartAsPNG(new File(plot, "linmotPosition.png"), jFreeChart, WIDTH, HEIGHT);
  }

  public void exportLinmotTemperature() throws IOException {
    VisualSet visualSet = new VisualSet(ColorDataLists._097.cyclic().deriveWithAlpha(192));
    visualSet.setPlotLabel("Brake temperature");
    visualSet.setAxesLabelX("time [s]");
    visualSet.setAxesLabelY("temperature [degC]");
    {
      Tensor tensor = map.get(LinmotGetVehicleChannel.INSTANCE);
      Tensor domain = tensor.get(Tensor.ALL, 0);
      visualSet.add(domain, tensor.get(Tensor.ALL, 2)).setLabel("winding 1");
      visualSet.add(domain, tensor.get(Tensor.ALL, 3)).setLabel("winding 2");
    }
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    ChartUtils.saveChartAsPNG(new File(plot, "linmotTemperature.png"), jFreeChart, WIDTH, HEIGHT);
  }

  public void exportVmu931acc() throws IOException {
    VisualSet visualSet = new VisualSet(ColorDataLists._097.cyclic().deriveWithAlpha(64));
    visualSet.setPlotLabel("VMU931 acceleration");
    visualSet.setAxesLabelX("time [s]");
    visualSet.setAxesLabelY("acceleration [m*s^-2]");
    Tensor tensor = map.get(Vmu931ImuVehicleChannel.INSTANCE);
    Tensor domain = tensor.get(Tensor.ALL, 0);
    visualSet.add(domain, tensor.get(Tensor.ALL, 2)).setLabel("x (forward)");
    visualSet.add(domain, tensor.get(Tensor.ALL, 3)).setLabel("y (left)");
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    ChartUtils.saveChartAsPNG(new File(plot, "vmu931acc.png"), jFreeChart, WIDTH, HEIGHT);
  }

  public void exportVmu931accSmooth() throws IOException {
    VisualSet visualSet = new VisualSet(ColorDataLists._097.cyclic().deriveWithAlpha(192));
    visualSet.setPlotLabel("Smoothed Acceleration from VMU931");
    visualSet.setAxesLabelX("time [s]");
    visualSet.setAxesLabelY("acceleration [m*s^-2]");
    {
      Tensor tensor = map.get(Vmu931ImuVehicleChannel.INSTANCE);
      Tensor domain = tensor.get(Tensor.ALL, 0);
      Tensor mask = new WindowCenterSampler(GaussianWindow.FUNCTION).apply(100);
      Tensor smoothX = ListConvolve.of(mask, tensor.get(Tensor.ALL, 2));
      Tensor smoothY = ListConvolve.of(mask, tensor.get(Tensor.ALL, 3));
      visualSet.add(domain.extract(0, smoothX.length()), smoothX).setLabel("x (forward)");
      visualSet.add(domain.extract(0, smoothY.length()), smoothY).setLabel("y (left)");
    }
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    ChartUtils.saveChartAsPNG(new File(plot, "vmu931accSmooth.png"), jFreeChart, WIDTH, HEIGHT);
  }

  public void exportPose() throws IOException {
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel("Pose");
    visualSet.setAxesLabelX("time [s]");
    Tensor tensor = map.get(GokartPoseChannel.INSTANCE);
    Tensor domain = tensor.get(Tensor.ALL, 0);
    visualSet.add(domain, tensor.get(Tensor.ALL, 1)).setLabel("global x position [m]");
    visualSet.add(domain, tensor.get(Tensor.ALL, 2)).setLabel("global y position [m]");
    visualSet.add(domain, tensor.get(Tensor.ALL, 3)).setLabel("global heading [rad]");
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    ChartUtils.saveChartAsPNG(new File(plot, "pose_raw.png"), jFreeChart, WIDTH, HEIGHT);
  }

  public void exportPoseSmooth() throws IOException {
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel("Smoothed Pose");
    visualSet.setAxesLabelX("time [s]");
    Tensor tensor = Import.of(new File(plot.getParentFile(), DynamicsConversion.GOKART_POSE_SMOOTH + ".csv.gz"));
    Tensor domain = tensor.get(Tensor.ALL, 0);
    visualSet.add(domain, tensor.get(Tensor.ALL, 1)).setLabel("global x position [m]");
    visualSet.add(domain, tensor.get(Tensor.ALL, 2)).setLabel("global y position [m]");
    visualSet.add(domain, tensor.get(Tensor.ALL, 3)).setLabel("global heading [rad]");
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    ChartUtils.saveChartAsPNG(new File(plot, "pose_smooth.png"), jFreeChart, WIDTH, HEIGHT);
  }

  public void exportPoseDerivative() throws IOException {
    final Scalar hertz = RealScalar.of(20.0);
    Tensor tensor = Import.of(new File(plot.getParentFile(), DynamicsConversion.GOKART_POSE_SMOOTH + ".csv.gz"));
    LieDifferences lieDifferences = new LieDifferences(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
    Tensor refined = Tensor.of(tensor.stream().map(row -> row.extract(1, 4)));
    Tensor speeds = lieDifferences.apply(refined);
    Tensor times = tensor.get(Tensor.ALL, 0);
    Tensor domain = times.extract(0, tensor.length() - 1);
    {
      VisualSet visualSet = new VisualSet();
      visualSet.setPlotLabel("Derivatives from Smoothed Pose");
      visualSet.setAxesLabelX("time [s]");
      visualSet.add(domain, speeds.get(Tensor.ALL, 0).multiply(hertz)).setLabel("tangent velocity [m/s]");
      visualSet.add(domain, speeds.get(Tensor.ALL, 1).multiply(hertz)).setLabel("side slip [m/s]");
      JFreeChart jFreeChart = ListPlot.of(visualSet);
      ChartUtils.saveChartAsPNG(new File(plot, "speeds.png"), jFreeChart, WIDTH, HEIGHT);
    }
    {
      VisualSet visualSet = new VisualSet(ColorDataLists._097.cyclic().deriveWithAlpha(192));
      visualSet.setPlotLabel("Rotational Rate");
      visualSet.setAxesLabelX("time [s]");
      visualSet.setAxesLabelY("gyro [rad*s^-1]");
      {
        VisualRow visualRow = visualSet.add(domain, speeds.get(Tensor.ALL, 2).multiply(hertz));
        visualRow.setLabel("from smoothed pose [rad/s]");
        visualRow.setStroke(new BasicStroke(2f));
      }
      {
        Tensor vmu931 = map.get(Vmu931ImuVehicleChannel.INSTANCE);
        visualSet.add(vmu931.get(Tensor.ALL, 0), vmu931.get(Tensor.ALL, 4)).setLabel("from VMU931");
      }
      JFreeChart jFreeChart = ListPlot.of(visualSet);
      ChartUtils.saveChartAsPNG(new File(plot, "vmu931gyro.png"), jFreeChart, WIDTH, HEIGHT);
    }
  }

  public static void main(String[] args) throws IOException {
    File directory = new File("/media/datahaki/data/gokart/dynamics/20190208T145312_04");
    LogReport logReport = new LogReport(directory);
    logReport.exportStatus();
    logReport.exportSteerGet();
    logReport.exportRimoPut();
    logReport.exportRimoGet();
    logReport.exportLinmotPosition();
    logReport.exportLinmotTemperature();
    logReport.exportVmu931acc();
    logReport.exportVmu931accSmooth();
    logReport.exportPose();
    logReport.exportPoseSmooth();
    logReport.exportPoseDerivative();
  }
}
