package in.zeko.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;

import in.zeko.enums.ResponseStatus;
import in.zeko.enums.StepCountWebServiceRequestType;
import in.zeko.statistics.RequestStat;
import in.zeko.statistics.StatDaemon;
import in.zeko.utils.Phase;

public class StepCountWebServiceJob implements Runnable {

  private static Client client;
  private WebServiceJobMeta metaData;
  private CyclicBarrier startBarrier;
  private CyclicBarrier endBarrier;
  private int testsPerPhase;
  private Phase phase;

  // objects dealing with request making
  String appUri;

  // stats
  List<RequestStat> requestStats;
  int threadId;

  /**
   * Constructor.
   */
  public StepCountWebServiceJob(WebServiceJobMeta metaData,
                             CyclicBarrier startBarrier,
                             CyclicBarrier endBarrier,
                             Phase phase,
                             int threadId) {

    this.client         = ClientBuilder.newClient();
    this.metaData       = metaData;
    this.startBarrier   = startBarrier;
    this.endBarrier     = endBarrier;
    this.phase          = phase;
    this.testsPerPhase  = phase.getTestsPerPhase();

    this.requestStats = new ArrayList<RequestStat>();
    this.threadId = threadId;

    initialize();
  }

  private void initialize() {

    if (this.metaData.getApplicationPort() != 0) {
      this.appUri = this.metaData.getServerIp() +
              ":" + this.metaData.getApplicationPort();
    } else {

      // AWS lambda
      this.appUri = this.metaData.getServerIp();
    }

  }

  private void firePostStepCount(int[] requestData) {

    MultivaluedHashMap<String, String> body = new MultivaluedHashMap<>();

    body.add("userId", "" + requestData[0]);
    body.add("timeInterval", "" + requestData[1]);
    body.add("stepCount", "" + requestData[2]);
    body.add("day", "1");

    long currStart = System.currentTimeMillis();

    try {
      WebTarget target = client.target(this.appUri + metaData.getEndpoint().getEndpoint("postStepCount"));
      Response response = target.request(MediaType.APPLICATION_FORM_URLENCODED).post(Entity.form(body));
      response.close();
    } catch (Exception e) {
      System.out.println(e);
      long currEnd = System.currentTimeMillis();
      long currLatency = currEnd - currStart;
      requestStats.add(new RequestStat(StepCountWebServiceRequestType.POST_STEP_COUNT, ResponseStatus.FAILED, currLatency, phase.getTag()));
    }

    long currEnd = System.currentTimeMillis();
    long currLatency = currEnd - currStart;
    requestStats.add(new RequestStat(StepCountWebServiceRequestType.POST_STEP_COUNT, ResponseStatus.SUCCESSFUL, currLatency, phase.getTag()));

  }

  private void fireCurrentStepCount(int[] requestData) {

    long currStart = System.currentTimeMillis();

    try {
      WebTarget target = client.target(this.appUri + metaData.getEndpoint().getEndpoint("getCurrentStepCount"))
              .queryParam("userId", "" + requestData[0]);
      Response response = target.request().get();
      response.close();
    } catch (Exception e) {
      System.out.println(e);
      long currEnd = System.currentTimeMillis();
      long currLatency = currEnd - currStart;
      requestStats.add(new RequestStat(StepCountWebServiceRequestType.GET_CURRENT_STEP_COUNT, ResponseStatus.FAILED, currLatency, phase.getTag()));
    }

    long currEnd = System.currentTimeMillis();
    long currLatency = currEnd - currStart;
    requestStats.add(new RequestStat(StepCountWebServiceRequestType.GET_CURRENT_STEP_COUNT, ResponseStatus.SUCCESSFUL, currLatency, phase.getTag()));

  }

  private void fireSingleDayStepCount(int[] requestData) {

    long currStart = System.currentTimeMillis();

    try {
      WebTarget target = client.target(this.appUri + metaData.getEndpoint().getEndpoint("getSingleDayStepCount"))
              .queryParam("userId", "" + requestData[0])
              .queryParam("day", "1");
      Response response = target.request().get();
      response.close();
    } catch (Exception e) {
      System.out.println(e);
      long currEnd = System.currentTimeMillis();
      long currLatency = currEnd - currStart;
      requestStats.add(new RequestStat(StepCountWebServiceRequestType.GET_SINGLE_DAY_STEP_COUNT, ResponseStatus.FAILED, currLatency, phase.getTag()));
    }

    long currEnd = System.currentTimeMillis();
    long currLatency = currEnd - currStart;
    requestStats.add(new RequestStat(StepCountWebServiceRequestType.GET_SINGLE_DAY_STEP_COUNT, ResponseStatus.SUCCESSFUL, currLatency, phase.getTag()));

  }

  private int[] generateRandomData() {

    int[] data = new int[3];

    data[0] = ThreadLocalRandom.current().nextInt(phase.getUserPopulation());
    data[1] = ThreadLocalRandom.current().nextInt(phase.getStartInterval(), phase.getEndInterval() + 1);
    data[2] = ThreadLocalRandom.current().nextInt(phase.getMaxStepCount());

    return data;
  }

  /**
   * Initiates one iteration of jobs to the target.
   *
   * @return true if iteration ran successfully
   */
  private void doSingleTest() {

    int[] randomData1 = generateRandomData();
    int[] randomData2 = generateRandomData();
    int[] randomData3 = generateRandomData();


    firePostStepCount(randomData1);
    firePostStepCount(randomData2);
    fireCurrentStepCount(randomData1);
    fireSingleDayStepCount(randomData2);
    firePostStepCount(randomData3);

  }

  private void releaseResources() {
    client.close();
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread,
   * starting the thread causes the object's <code>run</code> method to be called in that separately
   * executing thread. <p> The general contract of the method <code>run</code> is that it may take
   * any action whatsoever.
   *
   * @see Thread#run()
   */
  public void run() {

    try {
      startBarrier.await();
    } catch (InterruptedException e) {
      System.out.println("Threads interrupted!");
    } catch (BrokenBarrierException e) {
      System.out.println("Thread Barrier broken!");
    }

    long start = System.currentTimeMillis();

    int phaseLength = phase.getEndInterval() - phase.getStartInterval() + 1;
    int testCount = testsPerPhase * phaseLength;

    for (int i = 0; i < testCount; i++) {
      doSingleTest();
    }

    long end = System.currentTimeMillis();

    StatDaemon.getInstance().addStats(requestStats);
//    LoadTestStats.getInstance().addJobResult(new JobResult(phase, requestStats, end-start, threadId));

    try {
      endBarrier.await();
    } catch (InterruptedException e) {
      System.out.println("Threads interrupted!");
    } catch (BrokenBarrierException e) {
      System.out.println("Thread Barrier broken!");
    }

    releaseResources();
  }
}
