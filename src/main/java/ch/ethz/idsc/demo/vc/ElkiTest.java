package ch.ethz.idsc.demo.vc;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import de.lmu.ifi.dbs.elki.algorithm.clustering.DBSCAN;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.datasource.ArrayAdapterDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.DatabaseConnection;
import de.lmu.ifi.dbs.elki.distance.distancefunction.minkowski.SquaredEuclideanDistanceFunction;

public enum ElkiTest {
  ;
  static double[][] fromMatrix(Tensor matrix) {
    final int cols = Unprotect.dimension1(matrix);
    double[][] array = new double[matrix.length()][cols];
    for (int row = 0; row < matrix.length(); ++row)
      for (int col = 0; col < cols; ++col)
        array[row][col] = matrix.Get(row, col).number().doubleValue();
    return array;
  }

  public static Database sample(Tensor p) {
    double[][] data = fromMatrix(p);
    DatabaseConnection dbc = new ArrayAdapterDatabaseConnection(data);
    Database db = new StaticArrayDatabase(dbc, null);
    db.initialize();
    return db;
  }

  static Database sample() {
    double[][] data = new double[10][2];
    data[3][0] = 0.98;
    data[3][1] = 0.1;
    data[0][0] = 0.99;
    data[0][1] = 0.16;
    data[2][0] = 2.16;
    data[2][1] = 3.99;
    data[1][0] = 2.1;
    data[1][1] = 3.98;
    System.out.println(Arrays.deepToString(data));
    DatabaseConnection dbc = new ArrayAdapterDatabaseConnection(data);
    Database db = new StaticArrayDatabase(dbc, null);
    db.initialize();
    return db;
  }

  public static void testDBSCANResults(Tensor p) {
    Database db = sample(p);
    long nanoTime = System.nanoTime();
    DBSCAN<NumberVector> dbscan = new DBSCAN<>(SquaredEuclideanDistanceFunction.STATIC, 1.5, 5);
    Clustering<Model> result = dbscan.run(db);
    // new ELKIBuilder<DBSCAN<DoubleVector>>(DBSCAN.class) //
    // .with(DBSCAN.Parameterizer.EPSILON_ID, 0.04) //
    // .with(DBSCAN.Parameterizer.MINPTS_ID, 20) //
    // .build().run(db);
    long nanoTime2 = System.nanoTime();
    System.out.println((nanoTime2 - nanoTime) * 0.000001 + "ms");
    List<Cluster<Model>> allClusters = result.getAllClusters();
    // System.out.println(allClusters.size());
    for (Cluster<Model> cluster : allClusters) {
      System.out.println(cluster.size());
      System.out.println(cluster.isNoise());
      // DBIDs ids = cluster.getIDs();
      // // ids.
      // for (DBIDIter iter = ids.iter(); iter.valid(); iter.advance()) {
      // // DBID id = iter.
      // System.out.println(iter);
      // // iter.
      // }
      // System.out.println(ids.getClass().getName());
    }
    // System.out.println(iDs);
    System.out.println("end");
  }

  public static void testDBSCANResults() {
    Database db = sample();
    long nanoTime = System.nanoTime();
    DBSCAN<NumberVector> dbscan = new DBSCAN<>(SquaredEuclideanDistanceFunction.STATIC, 0.4, 2);
    Clustering<Model> result = dbscan.run(db);
    long nanoTime2 = System.nanoTime();
    System.out.println((nanoTime2 - nanoTime) * 0.000001 + "ms");
    List<Cluster<Model>> allClusters = result.getAllClusters();
    for (Cluster<Model> cluster : allClusters) {
      System.out.println(cluster.size());
      System.out.println(cluster.isNoise());
      DBIDs ids = cluster.getIDs();
      for (DBIDIter iter = ids.iter(); iter.valid(); iter.advance()) {
        System.out.println(iter);
      }
    }
    System.out.println("end");
  }

  public static void main(String[] args) {
    testDBSCANResults();
  }
}
