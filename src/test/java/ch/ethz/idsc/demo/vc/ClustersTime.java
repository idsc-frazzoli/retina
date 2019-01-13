// code by vc
package ch.ethz.idsc.demo.vc;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import ch.ethz.idsc.gokart.core.perc.ElkiDatabase;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.io.Timing;
import de.lmu.ifi.dbs.elki.algorithm.clustering.DBSCAN;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDRange;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.distance.distancefunction.minkowski.SquaredEuclideanDistanceFunction;

/* package */ enum ClustersTime {
  ;
  public static Tensor elkiDBSCAN(Tensor scans, double eps, int minPoints) {
    Tensor matrix = Flatten.of(scans, 1);
    int[] array = scans.stream().mapToInt(Tensor::length).toArray();
    NavigableMap<Integer, Integer> origin = new TreeMap<>();
    int sum = 0;
    for (int index = 0; index < array.length; index++) {
      origin.put(sum, index);
      sum = sum + array[index];
    }
    Database database = ElkiDatabase.from(matrix);
    Timing timing = Timing.started();
    DBSCAN<NumberVector> dbscan = //
        new DBSCAN<>(SquaredEuclideanDistanceFunction.STATIC, eps, minPoints);
    Clustering<Model> result = dbscan.run(database);
    long ns = timing.nanoSeconds();
    System.out.println((ns * 1e-6) + "ms");
    List<Cluster<Model>> allClusters = result.getAllClusters();
    Tensor pi = Tensors.empty();
    for (Cluster<Model> cluster : allClusters)
      if (!cluster.isNoise()) {
        NavigableMap<Integer, Tensor> map = new TreeMap<>();
        sum = 0;
        for (int index = 0; index < array.length; index++) {
          map.put(sum, Tensors.empty());
          sum = sum + array[index];
        }
        DBIDs ids = cluster.getIDs();
        Relation<NumberVector> rel = database.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);
        DBIDRange id = (DBIDRange) rel.getDBIDs();
        Set<Integer> set = new HashSet<>();
        for (DBIDIter iter = ids.iter(); iter.valid(); iter.advance()) {
          int offset = id.getOffset(iter);
          set.add(origin.floorEntry(offset).getValue());
          Entry<Integer, Tensor> floorEntry = map.floorEntry(offset);
          // floorEntry.
          floorEntry.getValue().append(matrix.get(offset));
        }
        // System.out.println(set);
        Tensor of = Tensor.of(map.values().stream());
        pi.append(of);
      }
    return pi;
  }
}
