// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.channel.Vmu931ImuXYZChannel;
import ch.ethz.idsc.gokart.offline.pose.Vmu931OdometryTable;
import ch.ethz.idsc.gokart.offline.tab.SingleChannelTable;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

/** code finds calibration of IMU and exports odometry since that point in time */
/* package */ enum OdometryExport {
  ;
  public static void run(GokartLogFile gokartLogFile) throws IOException {
    // ---
    OfflineTableSupplier tsPos = SingleChannelTable.of(GokartPoseChannel.INSTANCE);
    OfflineTableSupplier tsImu = SingleChannelTable.of(Vmu931ImuXYZChannel.INSTANCE);
    OfflineLogPlayer.process(DatahakiLogFileLocator.file(gokartLogFile), tsPos, tsImu);
    Tensor times = tsImu.getTable().get(Tensor.ALL, 0);
    Tensor dtime = Differences.of(times);
    List<Integer> list = IntStream.range(0, dtime.length()) //
        .filter(index -> Scalars.lessThan(RealScalar.ONE, dtime.Get(index))) //
        .boxed() //
        .collect(Collectors.toList());
    System.out.println(list);
    System.out.println(Dimensions.of(times));
    System.out.println(Dimensions.of(dtime));
    Tensor tensor = Tensor.of(list.stream().map(index -> times.Get(index)));
    System.out.println(tensor);
    // ---
    int index = list.get(list.size() - 1);
    System.out.println("ante=" + times.Get(index));
    // Tensor tsImuTable = tsImu.getTable();
    // Tensor tsImuPost = tsImuTable.extract(index + 1, tsImuTable.length());
    Scalar post = times.Get(index + 1);
    System.out.println("post=" + post);
    Tensor tsPosPost = Tensor.of(tsPos.getTable().stream().filter(row -> Scalars.lessThan(post, row.Get(0))));
    System.out.println(Dimensions.of(tsPosPost));
    File folder = HomeDirectory.Documents("drift", gokartLogFile.getTitle());
    folder.mkdirs();
    System.out.println("write pos");
    Export.of(new File(folder, "pos.csv.gz"), tsPosPost);
    // System.out.println("write imu");
    // Export.of(new File(folder, "imu.csv.gz"), tsImuPost);
    Vmu931OdometryTable vmu931OdometryTable = new Vmu931OdometryTable(Quantity.of(post, SI.SECOND));
    vmu931OdometryTable.vmu931Odometry.resetPose(PoseHelper.attachUnits(tsPosPost.get(0).extract(1, 4)));
    System.out.println("process");
    OfflineLogPlayer.process(DatahakiLogFileLocator.file(gokartLogFile), vmu931OdometryTable);
    Tensor tsOdoPost = vmu931OdometryTable.getTable(); // 1000[Hz]
    int steps = 20;
    Tensor tsOdoPost50 = Tensor.of(IntStream.range(0, tsOdoPost.length() / steps) //
        .map(i -> i * steps) //
        .mapToObj(tsOdoPost::get));
    System.out.println("write odo");
    Export.of(new File(folder, "odo.csv.gz"), tsOdoPost50);
  }

  public static void main(String[] args) throws IOException {
    // run(GokartLogFile._20190530T143412_1f4048bb);
    run(GokartLogFile._20190701T174938_12dcbfa8);
    // run(GokartLogFile._20190729T140711_23a2aa6f);
    // run(GokartLogFile._20190819T120821_c21b2aba);
  }
}
