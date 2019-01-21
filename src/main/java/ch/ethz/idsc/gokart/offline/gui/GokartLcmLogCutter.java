// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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

import ch.ethz.idsc.gokart.lcm.LcmLogFileCutter;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

/** GUI to inspect a log, and select and extract parts into new log files */
public class GokartLcmLogCutter {
  public static final String LCM_FILE = "log.lcm";
  public static final String GOKART_LOG_CONFIG = "GokartLogConfig.properties";
  private static final Font FONT = //
      new Font(Font.DIALOG, Font.PLAIN, GokartLcmImage.FX + 2);
  // ---
  private final JFrame jFrame = new JFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final NavigableMap<Integer, Integer> map = new TreeMap<>();
  // ---
  private final GokartLogFileIndexer gokartLogFileIndexer;
  private final String title;
  private final File export_root;
  private final BufferedImage bufferedImage;
  private final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      graphics.drawImage(bufferedImage, 0, 0, null);
      {
        graphics.setFont(FONT);
        graphics.setColor(Color.WHITE);
        int piy = -2;
        int fx = GokartLcmImage.FX;
        graphics.drawString("autonomous", 0, piy += fx);
        graphics.drawString("pose quality", 0, piy += fx);
        graphics.drawString("steer", 0, piy += fx);
        graphics.drawString("gyro z", 0, piy += fx);
        graphics.drawString("tire L", 0, piy += fx);
        graphics.drawString("tire R", 0, piy += fx);
      }
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
    private Point pressed = null;

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
        if (Objects.nonNull(pressed))
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
    public void actionPerformed(ActionEvent actionEvent) {
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

  /** @param gokartLogFileIndexer
   * @param export_root
   * @param title is the first part of the extracted log files
   * @throws Exception if export_root is not a directory and cannot be created */
  public GokartLcmLogCutter( //
      GokartLogFileIndexer gokartLogFileIndexer, //
      File export_root, //
      String title) {
    this.gokartLogFileIndexer = gokartLogFileIndexer;
    this.export_root = export_root;
    export_root.mkdir();
    if (!export_root.isDirectory())
      throw new RuntimeException(export_root.toString());
    this.title = title;
    bufferedImage = GokartLcmImage.of(gokartLogFileIndexer);
    // ---
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    windowConfiguration.attach(getClass(), jFrame);
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
    jScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
    jPanel.add(jScrollPane, BorderLayout.CENTER);
    jFrame.setContentPane(jPanel);
    // ---
    jFrame.setTitle(title);
    jFrame.setVisible(true);
  }
}
