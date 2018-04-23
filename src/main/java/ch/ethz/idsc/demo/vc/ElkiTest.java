package ch.ethz.idsc.demo.vc;

import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
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


  public static void testDBSCANResults(Tensor p) {
    Database db = sample(p);
    long nanoTime = System.nanoTime();
    DBSCAN<NumberVector> dbscan = new DBSCAN<>(SquaredEuclideanDistanceFunction.STATIC, 1.5, 5);
    Clustering<Model> result = dbscan.run(db);
    long nanoTime2 = System.nanoTime();
    System.out.println((nanoTime2 - nanoTime) * 0.000001 + "ms");
    List<Cluster<Model>> allClusters = result.getAllClusters();
    // System.out.println(allClusters.size());
    for (Cluster<Model> cluster : allClusters) {
      Tensor pr = Tensors.empty();
      System.out.println("Cluster size:" + cluster.size());
      System.out.println("Is noise:" + cluster.isNoise());
      if (cluster.size() < 100) {
        DBIDs ids = cluster.getIDs();
        Relation<NumberVector> rel = db.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);
        DBIDRange id = (DBIDRange) rel.getDBIDs();
        for (DBIDIter iter = ids.iter(); iter.valid(); iter.advance()) {
          int offset = id.getOffset(iter);
          // System.out.println("offset:" + offset);
          // System.out.println(p.get(offset));
          pr.append(Tensors.of(p.get(offset)));
         
        }
        System.out.println(pr);
      }
    }
    System.out.println("end");
  }

  public static void main(String[] args) {
    double[][] data = new double[10][2];
    data[3][0] = 0.98;
    data[3][1] = 0.1;
    data[0][0] = 0.99;
    data[0][1] = 0.16;
    data[2][0] = 2.16;
    data[2][1] = 3.99;
    data[1][0] = 2.1;
    data[1][1] = 3.98;
    Tensor p = Tensors.matrixDouble(data);
   testDBSCANResults(p);
  }
}
