// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;

public class Vmu931Recorder implements Vmu931Listener {
  private final TableBuilder tableBuilderAcc = new TableBuilder();
  private final TableBuilder tableBuilderGyr = new TableBuilder();
  private final int rows;
  private boolean flagA = true;
  private boolean flagG = true;

  public Vmu931Recorder(int rows) {
    this.rows = rows;
  }

  @Override
  public void accelerometer(ByteBuffer byteBuffer) {
    if (tableBuilderAcc.getRowCount() < rows) {
      int timestamp_ms = byteBuffer.getInt();
      /** scalar has unit [deg*s^-1] */
      float x = byteBuffer.getFloat();
      float y = byteBuffer.getFloat();
      float z = byteBuffer.getFloat();
      tableBuilderAcc.appendRow(Tensors.vector(timestamp_ms, x, y, z));
    } else //
    if (tableBuilderAcc.getRowCount() == rows && flagA)
      try {
        flagA = false;
        System.out.println("EXPORTED ACC");
        Export.of(HomeDirectory.file("vmu931acc.csv"), tableBuilderAcc.toTable());
      } catch (IOException e) {
        e.printStackTrace();
      }
  }

  @Override
  public void gyroscope(ByteBuffer byteBuffer) {
    if (tableBuilderGyr.getRowCount() < rows) {
      int timestamp_ms = byteBuffer.getInt();
      /** scalar has unit [deg*s^-1] */
      float x = byteBuffer.getFloat();
      float y = byteBuffer.getFloat();
      float z = byteBuffer.getFloat();
      tableBuilderGyr.appendRow(Tensors.vector(timestamp_ms, x, y, z));
    } else //
    if (tableBuilderGyr.getRowCount() == rows && flagG)
      try {
        flagG = false;
        System.out.println("EXPORTED GYRO");
        Export.of(HomeDirectory.file("vmu931gyro.csv"), tableBuilderGyr.toTable());
      } catch (IOException e) {
        e.printStackTrace();
      }
  }
}
