// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.lcm.LcmLogFileCutter;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ImageResize;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.sca.Clip;

public class GokartLcmLogCutter {
  public static final String LCM_FILE = "log.lcm";
  public static final String GOKART_LOG_CONFIG = "GokartLogConfig.properties";
  // ---
  private final GokartLogFileIndexer gokartLogFileIndexer;
  private final String title;
  private final File export_root;
  private final BufferedImage bufferedImage;
  private final JFrame jFrame = new JFrame();
  private final NavigableMap<Integer, Integer> map = new TreeMap<>();
  private Point pressed = null;
  private final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      graphics.drawImage(bufferedImage, 0, 0, null);
      int ofsy = 20;
      synchronized (map) {
        for (Entry<Integer, Integer> entry : map.entrySet()) {
          int x0 = entry.getKey();
          int width = Math.max(0, entry.getValue() - x0);
          graphics.setColor(new Color(0, 0, 255, 128));
          graphics.fillRect(x0, ofsy, width, 32);
          graphics.setColor(new Color(255, 255, 255, 128));
          graphics.drawRect(x0, ofsy, width, 32);
        }
      }
    }
  };
  private final MouseAdapter mouseListener = new MouseAdapter() {
    @Override
    public void mousePressed(MouseEvent mouseEvent) {
      if (mouseEvent.getButton() == 3) {
        synchronized (map) {
          Entry<Integer, Integer> lowerEntry = map.lowerEntry(mouseEvent.getX());
          if (Objects.nonNull(lowerEntry))
            map.remove(lowerEntry.getKey());
        }
        jComponent.repaint();
      } else
        pressed = mouseEvent.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
      synchronized (map) {
        map.put(pressed.x, mouseEvent.getX());
      }
      jComponent.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
      pressed = null;
    }
  };
  private final ActionListener actionListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      synchronized (map) {
        NavigableMap<Integer, Integer> navigableMap = new TreeMap<>();
        for (Entry<Integer, Integer> entry : map.entrySet()) {
          int x0 = gokartLogFileIndexer.getEventIndex(entry.getKey());
          int x1 = gokartLogFileIndexer.getEventIndex(entry.getValue());
          // Integer last = navigableMap.lastKey();
          if (navigableMap.isEmpty() || navigableMap.lastKey() < x0)
            if (x0 < x1)
              navigableMap.put(x0, x1);
        }
        // ---
        System.out.println(navigableMap);
        try {
          new LcmLogFileCutter(gokartLogFileIndexer.file(), navigableMap) {
            @Override
            public File filename(int count) {
              File folder = new File(export_root, String.format("%s_%d", title, count + 1));
              folder.mkdir();
              if (!folder.isDirectory())
                throw new RuntimeException();
              try {
                new File(folder, GOKART_LOG_CONFIG).createNewFile();
              } catch (IOException e) {
                e.printStackTrace();
              }
              return new File(folder, LCM_FILE);
            }
          };
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    }
  };

  public GokartLcmLogCutter(GokartLogFileIndexer gokartLogFileIndexer, File export_root, String title) {
    this.gokartLogFileIndexer = gokartLogFileIndexer;
    this.export_root = export_root;
    this.title = title;
    bufferedImage = speedProfile(gokartLogFileIndexer);
    // ---
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 100, 1500, 200);
    JPanel jPanel = new JPanel(new BorderLayout());
    {
      JToolBar jToolBar = new JToolBar();
      jToolBar.setFloatable(false);
      jToolBar.setLayout(new FlowLayout(FlowLayout.RIGHT, 3, 0));
      JButton jButton = new JButton("export");
      jButton.addActionListener(actionListener);
      jToolBar.add(jButton);
      jPanel.add(jToolBar, BorderLayout.NORTH);
    }
    jComponent.setPreferredSize(new Dimension(bufferedImage.getWidth(), 0));
    jComponent.addMouseListener(mouseListener);
    jComponent.addMouseMotionListener(mouseListener);
    JScrollPane jScrollPane = new JScrollPane(jComponent, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    jPanel.add(jScrollPane, BorderLayout.CENTER);
    jFrame.setContentPane(jPanel);
    // ---
    jFrame.setTitle(title);
    jFrame.setVisible(true);
  }

  static BufferedImage speedProfile(GokartLogFileIndexer gokartLogFileIndexer) {
    Tensor im = Tensors.empty();
    {
      Tensor auton = Transpose.of(Tensor.of(gokartLogFileIndexer.raster2auton()));
      System.out.println(Dimensions.of(auton));
      auton = ImageResize.nearest(auton.map(ColorDataGradients.COPPER), 8, 1);
      im.append(auton);
    }
    {
      Tensor poseq = Transpose.of(Tensor.of(gokartLogFileIndexer.raster2poseq()));
      System.out.println(Dimensions.of(poseq));
      poseq = ImageResize.nearest(poseq.map(ColorDataGradients.AVOCADO), 8, 1);
      im.append(poseq);
    }
    {
      Clip clip = Clip.function(-0.7, 0.7);
      Tensor steer = Transpose.of(Tensor.of(gokartLogFileIndexer.raster2steer()).map(clip::rescale));
      System.out.println(Dimensions.of(steer));
      steer = ImageResize.nearest(steer.map(ColorDataGradients.THERMOMETER), 8, 1);
      im.append(steer);
    }
    {
      Clip clip = Clip.function(-1, +1);
      Tensor gyroz = Transpose.of(Tensor.of(gokartLogFileIndexer.raster2gyroz()).map(clip::rescale));
      System.out.println(Dimensions.of(gyroz));
      gyroz = ImageResize.nearest(gyroz.map(ColorDataGradients.THERMOMETER), 8, 1);
      im.append(gyroz);
    }
    {
      Clip clip = Clip.function(0, 40);
      Tensor speed = Transpose.of( //
          Tensor.of(gokartLogFileIndexer.raster2speed()).map(clip::rescale));
      System.out.println(Dimensions.of(speed));
      speed = ImageResize.nearest(speed.map(ColorDataGradients.CLASSIC), 16, 1);
      im.append(speed);
    }
    im = Flatten.of(im, 1);
    System.out.println(Dimensions.of(im));
    return ImageFormat.of(im);
  }
}
