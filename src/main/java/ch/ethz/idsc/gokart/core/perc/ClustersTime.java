// code by vc
package ch.ethz.idsc.gokart.core.perc;

import java.util.List;
import java.util.TreeMap;

import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Flatten;
import de.lmu.ifi.dbs.elki.algorithm.clustering.DBSCAN;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDRange;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.datasource.ArrayAdapterDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.DatabaseConnection;
import de.lmu.ifi.dbs.elki.distance.distancefunction.minkowski.SquaredEuclideanDistanceFunction;

public enum ClustersTime {
  ;
  /** @param matrix
   * @return tensor of clusters */
  // TODO remove print outs. provide timing and properties in separate class if necessary
  // TODO also handle empty input
  public static Tensor elkiDBSCAN(Tensor scans, double eps, int minPoints) {
    Tensor matrix = Flatten.of(scans, 1);
    int[] array = scans.stream().mapToInt(Tensor::length).toArray();
    Database database = ClustersTime.sample(matrix);
    Stopwatch stopwatch = Stopwatch.started();
    DBSCAN<NumberVector> dbscan = //
        new DBSCAN<>(SquaredEuclideanDistanceFunction.STATIC, eps, minPoints);
    Clustering<Model> result = dbscan.run(database);
    long ns = stopwatch.display_nanoSeconds();
    System.out.println((ns * 1e-6) + "ms");
    List<Cluster<Model>> allClusters = result.getAllClusters();
    Tensor pi = Tensors.empty();
    for (Cluster<Model> cluster : allClusters)
      if (!cluster.isNoise()) {
        TreeMap<Integer, Tensor> map = new TreeMap<>();
        int sum = 0;
        for (int index = 0; index < array.length; index++) {
          map.put(sum, Tensors.empty());
          sum = sum + array[index];
        }
        DBIDs ids = cluster.getIDs();
        Relation<NumberVector> rel = database.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);
        DBIDRange id = (DBIDRange) rel.getDBIDs();
        for (DBIDIter iter = ids.iter(); iter.valid(); iter.advance()) {
          int offset = id.getOffset(iter);
          map.floorEntry(offset).getValue().append(matrix.get(offset));
        }
        Tensor of = Tensor.of(map.values().stream());
        pi.append(of);
      }
    return pi;
  }

  static Database sample(Tensor matrix) {
    double[][] data = fromMatrix(matrix);
    DatabaseConnection databaseConnection = new ArrayAdapterDatabaseConnection(data);
    Database database = new StaticArrayDatabase(databaseConnection, null);
    database.initialize();
    return database;
  }

  // TODO TENSOR V052
  static double[][] fromMatrix(Tensor matrix) {
    final int cols = Unprotect.dimension1(matrix);
    double[][] array = new double[matrix.length()][cols];
    for (int row = 0; row < matrix.length(); ++row)
      for (int col = 0; col < cols; ++col)
        array[row][col] = matrix.Get(row, col).number().doubleValue();
    return array;
  }
}
