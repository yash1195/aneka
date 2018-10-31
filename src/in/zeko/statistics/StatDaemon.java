/**
 * @author yashchoukse
 */

package in.zeko.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class StatDaemon {

  private BlockingQueue<RequestStat> queue;

  private static StatDaemon instance = null;

  static {
    instance = new StatDaemon();
  }

  private StatDaemon() {

    queue = new LinkedBlockingQueue<>();
  }

  public void addStats(List<RequestStat> stats) {
    queue.addAll(stats);
  }

  public boolean isEmpty() {
    return queue.isEmpty();
  }

  public List<RequestStat> pollStats(int chunkSize) {

    List<RequestStat> stats = new ArrayList<>();

    while (chunkSize-- != 0) {
      RequestStat stat = queue.poll();
      if (stat == null) {
        return stats;
      }
      stats.add(stat);
    }
    return stats;
  }

  public static StatDaemon getInstance() {
    return instance;
  }
}
