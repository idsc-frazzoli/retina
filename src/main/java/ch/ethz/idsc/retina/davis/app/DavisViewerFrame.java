// code by jph
package ch.ethz.idsc.retina.davis.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.util.img.ImageCopy;
import ch.ethz.idsc.retina.util.img.TimedImageEvent;
import ch.ethz.idsc.retina.util.img.TimedImageListener;
import ch.ethz.idsc.retina.util.time.SystemTimestamp;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.tensor.io.HomeDirectory;

// TODO redraw thread is independent of sync signal of images...!
public class DavisViewerFrame implements TimedImageListener {
  private static final File EXPORT_DIRECTORY = HomeDirectory.Pictures("dvs");
  // ---
  public final JFrame jFrame = new JFrame();
  @SuppressWarnings("unused")
  private DavisEventStatistics davisEventStatistics;
  // private Tensor eventCount = Array.zeros(3);
  private final Timer timer = new Timer();
  boolean tallyAps = false;
  public final DavisViewerComponent davisViewerComponent = new DavisViewerComponent();
  public final DavisTallyProvider davisTallyProvider = new DavisTallyProvider( //
      davisTallyEvent -> {
        if (tallyAps)
          davisViewerComponent.davisTallyEvent = davisTallyEvent;
      });
  public final DvsTallyProvider dvsTallyProvider = new DvsTallyProvider( //
      davisTallyEvent -> {
        if (!tallyAps)
          davisViewerComponent.davisTallyEvent = davisTallyEvent;
      });
  boolean recording = false;
  private int counter = 0;
  private final File directory = HomeDirectory.Pictures(SystemTimestamp.asString());

  public DavisViewerFrame(DavisDevice davisDevice, AbstractAccumulatedImage abstractAccumulatedImage) {
    Component component = jFrame.getContentPane();
    JPanel jPanel = (JPanel) component;
    {
      JToolBar jToolBar = new JToolBar();
      jToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
      jToolBar.setFloatable(false);
      {
        JButton jButton = new JButton("exp");
        jButton.addActionListener(actionEvent -> {
          System.out.println("here");
          try {
            directory.mkdir();
            File file = new File(directory, String.format("dubi%04d.jpg", counter));
            System.out.println(file);
            ImageIO.write(davisViewerComponent.sigImage, "jpg", file);
            counter++;
          } catch (Exception exception) {
            // ---
          }
        });
        jToolBar.add(jButton);
      }
      {
        SpinnerLabel<Integer> spinnerLabel = new SpinnerLabel<>();
        spinnerLabel.addSpinnerListener(shift -> {
          davisTallyProvider.setShift(shift);
          dvsTallyProvider.setShift(shift);
        });
        spinnerLabel.setList(Arrays.asList(6, 7, 8, 9));
        spinnerLabel.setValueSafe(davisTallyProvider.getShift());
        spinnerLabel.addToComponentReduced(jToolBar, new Dimension(70, 28), "shift");
      }
      {
        SpinnerLabel<Integer> spinnerLabel = new SpinnerLabel<>();
        spinnerLabel.addSpinnerListener(interval -> abstractAccumulatedImage.setInterval(interval));
        spinnerLabel.setList(Arrays.asList(1_000, 2_500, 5_000, 10_000, 25_000, 50_000));
        spinnerLabel.setValueSafe(abstractAccumulatedImage.getInterval());
        spinnerLabel.addToComponentReduced(jToolBar, new Dimension(80, 28), "interval");
      }
      {
        JToggleButton jToggleButton = new JToggleButton("aps");
        jToggleButton.addActionListener(e -> {
          davisViewerComponent.aps = jToggleButton.isSelected();
          tallyAps = jToggleButton.isSelected();
        });
        jToolBar.add(jToggleButton);
      }
      {
        JToggleButton jToggleButton = new JToggleButton("record");
        jToggleButton.setToolTipText("record to SAE images to " + EXPORT_DIRECTORY.toString());
        jToggleButton.addActionListener(event -> recording = jToggleButton.isSelected());
        jToolBar.add(jToggleButton);
      }
      {
        JToggleButton jToggleButton = new JToggleButton("Rotate frame");
        jToggleButton.addActionListener(event -> abstractAccumulatedImage.setRotated(jToggleButton.isSelected()));
        jToolBar.add(jToggleButton);
      }
      jPanel.add(jToolBar, BorderLayout.NORTH);
    }
    jPanel.add(davisViewerComponent.jComponent, BorderLayout.CENTER);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent windowEvent) {
        timer.cancel();
      }
    });
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    // jFrame.setVisible(true);
    {
      TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
          davisViewerComponent.jComponent.repaint();
        }
      };
      timer.schedule(timerTask, 100, 33); // 33 ms -> 30 Hz
    }
    abstractAccumulatedImage.addListener(davisViewerComponent.timedImageListener);
    abstractAccumulatedImage.addListener(this);
  }

  public void setStatistics(DavisEventStatistics davisEventStatistics) {
    this.davisEventStatistics = davisEventStatistics;
  }

  private int count = -1;

  @Override // from TimedImageListener
  public void timedImage(TimedImageEvent timedImageEvent) {
    if (recording) {
      ImageCopy imageCopy = new ImageCopy();
      imageCopy.update(timedImageEvent.bufferedImage);
      BufferedImage bufferedImage = imageCopy.get();
      Graphics graphics = bufferedImage.createGraphics();
      graphics.setColor(Color.WHITE);
      graphics.drawString("" + timedImageEvent.time, 0, 10);
      try {
        System.out.println(timedImageEvent.time);
        EXPORT_DIRECTORY.mkdir();
        ImageIO.write(bufferedImage, "png", new File(EXPORT_DIRECTORY, String.format("dvs%07d.png", ++count)));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }
}
