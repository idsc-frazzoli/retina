package ch.ethz.idsc.demo.vc;

import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
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

enum ElkiTest {
  ;
  private static double[][] fromMatrix(Tensor matrix) {
    final int cols = Unprotect.dimension1(matrix);
    double[][] array = new double[matrix.length()][cols];
    for (int row = 0; row < matrix.length(); ++row)
      for (int col = 0; col < cols; ++col)
        array[row][col] = matrix.Get(row, col).number().doubleValue();
    return array;
  }

  private static Database sample() {
    double[][] data = new double[10][2];
    Distribution d = UniformDistribution.of(-1, 10);
    data = fromMatrix(RandomVariate.of(d, 16000, 2));
    // Adapter to load data from an existing array.
    DatabaseConnection dbc = new ArrayAdapterDatabaseConnection(data);
    // Create a database (which may contain multiple relations!)
    Database db = new StaticArrayDatabase(dbc, null);
    // Load the data into the database (do NOT forget to initialize...)
    db.initialize();
    return db;
  }

  public static void testDBSCANResults() {
    Database db = sample();
    long nanoTime = System.nanoTime();
    DBSCAN<NumberVector> dbscan = new DBSCAN<>(SquaredEuclideanDistanceFunction.STATIC, 0.04, 20);
    Clustering<Model> result = dbscan.run(db);
    // new ELKIBuilder<DBSCAN<DoubleVector>>(DBSCAN.class) //
    // .with(DBSCAN.Parameterizer.EPSILON_ID, 0.04) //
    // .with(DBSCAN.Parameterizer.MINPTS_ID, 20) //
    // .build().run(db);
    long nanoTime2 = System.nanoTime();
    System.out.println(nanoTime2 - nanoTime);
    List<Cluster<Model>> allClusters = result.getAllClusters();
    System.out.println(allClusters.size());
    for (Cluster<Model> cluster : allClusters) {
      System.out.println(cluster.size());
      System.out.println(cluster.isNoise());
      Model model = cluster.getModel();
      DBIDs ids = cluster.getIDs();
      // ids.
      for (DBIDIter iter = ids.iter(); iter.valid(); iter.advance()) {
        // DBID id = iter.
        // System.out.println(iter);
        // iter.
      }
      System.out.println(ids.getClass().getName());
    }
    // System.out.println(iDs);
  }

  public static void main(String[] args) {
    testDBSCANResults();
  }
}
