package ch.ethz.idsc.retina.demo.jph;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.painters.TracePainterDisc;

enum ChartDemo {
  ;
  public static void main(String[] args) {
    Chart2D chart = new Chart2D();
    ITrace2D trace = new Trace2DLtd(10, "name");
    chart.addTrace(trace);
    // add marker lines to the trace
    TracePainterDisc markerPainter = new TracePainterDisc();
    markerPainter.setDiscSize(2);
    trace.addTracePainter(markerPainter);
    // trace.addPoint(2, 3);
    // trace.addPoint(3, 4);
    // trace.addPoint(4, 1);
    // chart.removeAxisXBottom(chart.getAxesXBottom().iterator().next());
    // ---
    Random random = new Random();
    JFrame jFrame = new JFrame("asdf");
    jFrame.setContentPane(chart);
    jFrame.setBounds(100, 100, 300, 300);
    jFrame.setVisible(true);
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        trace.addPoint(System.currentTimeMillis(), random.nextDouble());
      }
    }, 100, 100);
  }
}
