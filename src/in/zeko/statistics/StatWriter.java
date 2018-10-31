/**
 * @author yashchoukse
 */

package in.zeko.statistics;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

import in.zeko.utils.StepCountWebServiceLoadTest;

public class StatWriter implements Runnable {

  private String fileUrl;
  private PrintWriter writer;
  private StepCountWebServiceLoadTest loadTest;
  private StatDaemon statDaemon;
  private CyclicBarrier barrier;

  private static int CHUNK_SIZE = 10;

  public StatWriter(String fileUrl, StepCountWebServiceLoadTest test, StatDaemon daemon, CyclicBarrier barrier) throws IOException {
    this.fileUrl          = fileUrl;
    this.writer           = new PrintWriter(new File(fileUrl));
    this.loadTest         = test;
    this.statDaemon       = daemon;
    this.barrier          = barrier;

    addHeader();
  }

  private void addHeader() {
    String header = "phase,requestType,responseStatus,timeStamp,latency";
    writer.println(header);
  }

  /**
   * Does not need to be synchronized as only one writer thread will exist.
   */
  @Override
  public void run() {
    while (!loadTest.isTestDone || !statDaemon.isEmpty()) {

      if (!statDaemon.isEmpty()) {
        List<RequestStat> stats = statDaemon.pollStats(CHUNK_SIZE);
        for (RequestStat stat : stats) {
          String data = stat.phase + "," + stat.requestType + "," + stat.responseStatus + "," + stat.timestamp + "," + stat.latency;
          writer.println(data);
        }
      }
    }

    releaseResources();

    try {
      barrier.await();
    } catch (Exception e) {
      System.out.println("Stat writer barrier exception" + e);
    }

  }

  private void releaseResources() {
    writer.close();
  }
}
