// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.lcm.davis.DavisLcmClient;
import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;

class RunDavisRgbCalibration implements DavisDvsListener {
  public static final int SIZE = 300;
  long evt_count;
  Color color = new Color(0, 0, 0);
  int rgb = 0;
  boolean toggle = false;
  Tensor stats = Tensors.empty();
  JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      graphics.setColor(color);
      graphics.fillRect(0, 0, SIZE, SIZE);
    }
  };

  public RunDavisRgbCalibration() {
    DavisLcmClient dlc = new DavisLcmClient(DavisSerial.FX2_02460045.name());
    dlc.addDvsListener(this);
    dlc.startSubscriptions();
    JFrame jFrame = new JFrame();
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 1200 - SIZE, SIZE, SIZE);
    jFrame.setContentPane(jComponent);
    // jFrame.setBackground(Color.BLACK);
    Timer timer = new Timer();
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        timer.cancel();
        try {
          Export.of(HomeDirectory.file("green.csv"), stats);
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    });
    jFrame.setVisible(true);
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        jComponent.repaint();
      }
    }, 10, 20);
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        Tensor row = Tensors.vector(rgb, color.getRed(), color.getGreen(), color.getBlue(), evt_count);
        System.out.println(row);
        stats.append(row);
        evt_count = 0;
        if (toggle) {
          ++rgb;
          rgb = Math.min(rgb, 255);
          // color = new Color(rgb, rgb, rgb);
          color = new Color(0, rgb, 0);
        }
        toggle ^= true;
        jComponent.repaint();
      }
    }, 20, 100);
  }

  public static void main(String[] args) {
    new RunDavisRgbCalibration();
  }

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    ++evt_count;
  }
}
