// code by vc
package ch.ethz.idsc.gokart.core.perc;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Flatten;
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

public enum ClustersTracking {
  ;
  /** @param matrix
   * @return tensor of clusters */
  // TODO remove print outs. provide timing and properties in separate class if necessary
  // TODO also handle empty input
  public static void elkiDBSCAN(ClusterCollection oldClusters, Tensor newScan, double eps, int minPoints) {
    Tensor scans = oldClusters.toMatrices().append(newScan);
    oldClusters.getCollection().forEach(ClusterDeque::appendEmpty);
    int sizeCollection = oldClusters.getCollection().size();
    int[] array = scans.stream().mapToInt(Tensor::length).toArray();
    NavigableMap<Integer, Integer> origin = partitionMap(array, Function.identity());
    Tensor matrix = Flatten.of(scans, 1);
    Database database = Clusters.sample(matrix);
    Stopwatch stopwatch = Stopwatch.started();
    DBSCAN<NumberVector> dbscan = //
        new DBSCAN<>(SquaredEuclideanDistanceFunction.STATIC, eps, minPoints);
    Clustering<Model> result = dbscan.run(database);
    long ns = stopwatch.display_nanoSeconds();
    System.out.println((ns * 1e-6) + "ms");
    List<Cluster<Model>> allClusters = result.getAllClusters();
    for (Cluster<Model> cluster : allClusters)
      if (!cluster.isNoise()) {
        NavigableMap<Integer, Tensor> map = partitionMap(array, i -> Tensors.empty());
        DBIDs ids = cluster.getIDs();
        Relation<NumberVector> rel = database.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);
        DBIDRange id = (DBIDRange) rel.getDBIDs();
        TreeSet<Integer> set = new TreeSet<>();
        for (DBIDIter iter = ids.iter(); iter.valid(); iter.advance()) {
          int offset = id.getOffset(iter);
          set.add(origin.floorEntry(offset).getValue());
          Entry<Integer, Tensor> floorEntry = map.floorEntry(offset);
          floorEntry.getValue().append(matrix.get(offset));
        }
        if (set.equals(Collections.singleton(sizeCollection))) {
          oldClusters.addToCollection(map.lastEntry().getValue());
        } else if (set.size() == 2 && set.contains(sizeCollection)) {
          Tensor points = map.lastEntry().getValue();
          ClusterDeque next = oldClusters.getCollection().get(set.iterator().next());
          next.replaceLast(points);
        } else if (set.size() == 3 && set.contains(sizeCollection)) {
          oldClusters.addToCollection(map.lastEntry().getValue());
          System.out.println("case 3");
        }
      }
    oldClusters.maintainUntil(sizeCollection);
  }

  private static <T> NavigableMap<Integer, T> partitionMap(int[] array, Function<Integer, T> function) {
    NavigableMap<Integer, T> navigableMap = new TreeMap<>();
    int sum = 0;
    for (int index = 0; index < array.length; ++index) {
      navigableMap.put(sum, function.apply(index));
      sum += array[index];
    }
    return navigableMap;
  }
}
