// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import ch.ethz.idsc.retina.davis.Aedat31FrameListener;
import ch.ethz.idsc.retina.davis.Aedat31Imu6Listener;
import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.davis.io.Aedat31FileSupplier;
import ch.ethz.idsc.retina.davis.io.Aedat31FrameEvent;
import ch.ethz.idsc.retina.davis.io.Aedat31Imu6Event;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;

class Aedat31TableExport implements DavisDvsListener, Aedat31FrameListener, Aedat31Imu6Listener {
  private final TableBuilder table_frame = new TableBuilder();
  private final TableBuilder table_imu6 = new TableBuilder();
  private final Map<Integer, Integer> map = new TreeMap<>();

  @Override
  public void frameEvent(Aedat31FrameEvent aedat31FrameEvent) {
    table_frame.appendRow(Tensors.vector(aedat31FrameEvent.getTime_us()));
  }

  @Override
  public void davisDvs(DavisDvsEvent aedat31PolarityEvent) {
    int key = aedat31PolarityEvent.time();
    map.put(key, map.containsKey(key) ? map.get(key) + 1 : 1);
  }

  @Override
  public void imu6Event(Aedat31Imu6Event aedat31Imu6Event) {
    table_imu6.appendRow( //
        RealScalar.of(aedat31Imu6Event.getTime_us()), //
        aedat31Imu6Event.getAccel(), aedat31Imu6Event.getGyro(), //
        aedat31Imu6Event.getTemperature().map(Magnitude.DEGREE_CELSIUS) //
    );
  }

  public TableBuilder table_polarity() {
    TableBuilder tableBuilder = new TableBuilder();
    for (Entry<Integer, Integer> entry : map.entrySet())
      tableBuilder.appendRow(Tensors.vector(entry.getKey(), entry.getValue()));
    return tableBuilder;
  }

  public static void main(String[] args) throws Exception {
    Aedat31FileSupplier davisEventProvider = //
        new Aedat31FileSupplier(Aedat31.LOG_04.file);
    Aedat31TableExport aedat31TableExport = new Aedat31TableExport();
    davisEventProvider.aedat31PolarityListeners.add(aedat31TableExport);
    davisEventProvider.aedat31FrameListeners.add(aedat31TableExport);
    davisEventProvider.aedat31Imu6Listeners.add(aedat31TableExport);
    davisEventProvider.start();
    System.out.println("done");
    Export.of(HomeDirectory.file("frames.csv"), aedat31TableExport.table_frame.toTable());
    System.out.println("write frames done");
    Export.of(HomeDirectory.file("imu6.csv"), aedat31TableExport.table_imu6.toTable());
    System.out.println("write imu6 done");
    Export.of(HomeDirectory.file("polarity.csv"), aedat31TableExport.table_polarity().toTable());
    System.out.println("write polarity done");
  }
}
