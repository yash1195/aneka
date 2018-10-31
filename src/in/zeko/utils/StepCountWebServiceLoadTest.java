package in.zeko.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

import in.zeko.jobs.StepCountWebServiceJob;
import in.zeko.jobs.WebServiceJobMeta;
import in.zeko.statistics.LoadTestStats;

public class StepCountWebServiceLoadTest {

  private List<Phase> phases;
  private int maxThreadCount;
  private WebServiceJobMeta metaData;
  public Boolean isTestDone = false;


  public StepCountWebServiceLoadTest(List<Phase> phases, int maxThreadCount,
                                  WebServiceJobMeta metaData) {
    this.phases         = phases;
    this.maxThreadCount = maxThreadCount;
    this.metaData       = metaData;
  }

  public void run() throws Exception {

    System.out.println("Client starting ...");
    System.out.println();

    LoadTestStats.getInstance().markTestStart();

    for (Phase phase : phases) {
      run(phase);
    }

    isTestDone = true;
    LoadTestStats.getInstance().markTestEnd();

  }

  public boolean isTestDone() {
    return isTestDone;
  }

  private List<Thread> createWorkers(Phase phase,
                                     CyclicBarrier startBarrier,
                                     CyclicBarrier endBarrier) {

    List<Thread> workers = new ArrayList<Thread>();

    for (int i = 0 ; i < phase.getActiveThreadCount() ; i++) {

      workers.add(new Thread(
              new StepCountWebServiceJob(
                      metaData,
                      startBarrier,
                      endBarrier,
                      phase,
                      i)));

    }

    return workers;
  }

  private void run(Phase phase) throws Exception {

    CyclicBarrier startBarrier  = new CyclicBarrier(phase.getActiveThreadCount() + 1);
    CyclicBarrier endBarrier    = new CyclicBarrier(phase.getActiveThreadCount() + 1);

    List<Thread> workers = createWorkers(phase, startBarrier, endBarrier);

    for (Thread worker : workers) {
      worker.start();
    }

    // open gate = note time
    startBarrier.await();

    System.out.println(phase.getTag() + " Phase: All threads running");

    long start = System.currentTimeMillis();
    // close gate = note time
    endBarrier.await();

    long end = System.currentTimeMillis();
    long diff = (end - start) / 1000;

    System.out.println(phase.getTag() + " Phase: Complete. Time: " + diff + " seconds");
    System.out.println();
  }
}
