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
import java.nio.ByteBuffer;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.lcm.LcmLogFileCutter;
import ch.ethz.idsc.gokart.offline.api.FirstLogMessage;
import ch.ethz.idsc.gokart.offline.api.GokartLogConfig;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import ch.ethz.idsc.tensor.io.TensorProperties;
import ch.ethz.idsc.tensor.sca.Round;

/** GUI to inspect a log, and select and extract parts into new log files */
public class GokartLcmLogCutter {
  public static final String LCM_FILE = "log.lcm";
  public static final String GOKART_LOG_CONFIG = "GokartLogConfig.properties";
  private static final Font FONT = //
      new Font(Font.DIALOG, Font.PLAIN, GokartLcmImage.FX + 2);
  // ---
  public final JFrame jFrame = new JFrame();
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
        JViewport jViewport = jScrollPane.getViewport();
        final int pix = jViewport.getViewPosition().x;
        graphics.setFont(FONT);
        graphics.setColor(Color.WHITE);
        int piy = -2;
        int fx = GokartLcmImage.FX;
        graphics.drawString("auton. button", pix, piy += fx);
        graphics.drawString("active steering", pix, piy += fx);
        graphics.drawString("pose quality", pix, piy += fx);
        graphics.drawString("steer", pix, piy += fx);
        graphics.drawString("gyro z", pix, piy += fx);
        graphics.drawString("tire L", pix, piy += fx);
        graphics.drawString("tire R", pix, piy += fx);
      }
      int ofsy = 28;
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
  private final JScrollPane jScrollPane = new JScrollPane(jComponent, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
  private final MouseAdapter mouseListener = new MouseAdapter() {
    private Point pressed = null;

    @Override // from MouseAdapter
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

    @Override // from MouseAdapter
    public void mouseDragged(MouseEvent mouseEvent) {
      synchronized (map) {
        if (Objects.nonNull(pressed))
          map.put(pressed.x, mouseEvent.getX());
      }
      jComponent.repaint();
    }

    @Override // from MouseAdapter
    public void mouseReleased(MouseEvent mouseEvent) {
      pressed = null;
    }
  };
  boolean csv = false;
  boolean htm = false;
  private final ActionListener actionListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
      synchronized (map) {
        NavigableMap<Integer, Integer> navigableMap = new TreeMap<>();
        for (Entry<Integer, Integer> entry : map.entrySet()) {
          int x0 = gokartLogFileIndexer.getEventIndex(entry.getKey());
          int size = gokartLogFileIndexer.getRasterSize();
          int last = Math.min(entry.getValue(), size - 1);
          int x1 = gokartLogFileIndexer.getEventIndex(last);
          // Integer last = navigableMap.lastKey();
          if (navigableMap.isEmpty() || navigableMap.lastKey() < x0)
            if (x0 < x1)
              navigableMap.put(x0, x1);
        }
        // ---
        System.out.println(navigableMap);
        try {
          final File date = new File(export_root, String.format("%s", title.substring(0, 8)));
          date.mkdir();
          // ---
          LcmLogFileCutter lcmLogFileCutter = new LcmLogFileCutter(gokartLogFileIndexer.file(), navigableMap) {
            @Override // from LcmLogFileCutter
            public File filename(int count) {
              File folder = new File(date, String.format("%s_%02d", title, count));
              folder.mkdir();
              return new File(folder, LCM_FILE);
            }
          };
          for (File file : lcmLogFileCutter.files())
            try {
              File config = new File(file.getParentFile(), GOKART_LOG_CONFIG);
              System.out.println(file);
              Optional<ByteBuffer> optional = FirstLogMessage.of(file, GokartPoseChannel.INSTANCE.channel());
              if (optional.isPresent()) {
                GokartPoseEvent gokartPoseEvent = GokartPoseEvent.of(optional.get());
                GokartLogConfig gokartLogConfig = new GokartLogConfig();
                gokartLogConfig.pose = gokartPoseEvent.getPose().map(Round._7);
                boolean save = TensorProperties.wrap(gokartLogConfig).trySave(config);
                if (!save)
                  System.err.println("did not save properties");
              } else
                config.createNewFile();
            } catch (Exception exception) {
              exception.printStackTrace();
            }
          // ---
          if (csv)
            for (File file : lcmLogFileCutter.files()) {
              File dest_folder = new File(file.getParentFile(), "csv");
              dest_folder.mkdir();
              File lcmFile = new File(file.getParentFile(), "log.lcm");
              ChannelCsvExport.of(new GokartLcmMap(lcmFile), dest_folder);
            }
          if (htm)
            for (File file : lcmLogFileCutter.files()) {
              File dest_folder = new File(file.getParentFile(), "htm");
              dest_folder.mkdir();
              File lcmFile = new File(file.getParentFile(), "log.lcm");
              new HtmlLogReport(new GokartLcmMap(lcmFile), file.getParentFile().getName(), dest_folder);
            }
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
      {
        JCheckBox jCheckBox = new JCheckBox("csv");
        jCheckBox.setSelected(csv);
        jCheckBox.addActionListener(actionEvent -> csv = jCheckBox.isSelected());
        jToolBar.add(jCheckBox);
      }
      {
        JCheckBox jCheckBox = new JCheckBox("htm");
        jCheckBox.setSelected(htm);
        jCheckBox.addActionListener(actionEvent -> htm = jCheckBox.isSelected());
        jToolBar.add(jCheckBox);
      }
      {
        JButton jButton = new JButton("export");
        jButton.addActionListener(actionListener);
        jToolBar.add(jButton);
      }
      jPanel.add(jToolBar, BorderLayout.NORTH);
    }
    jComponent.setPreferredSize(new Dimension(bufferedImage.getWidth(), 0));
    jComponent.addMouseListener(mouseListener);
    jComponent.addMouseMotionListener(mouseListener);
    jScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
    jPanel.add(jScrollPane, BorderLayout.CENTER);
    jFrame.setContentPane(jPanel);
    // ---
    jFrame.setTitle(title);
    jFrame.setVisible(true);
  }
}
