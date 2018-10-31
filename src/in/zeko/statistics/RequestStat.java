package in.zeko.statistics;

import java.util.Date;

import in.zeko.enums.ResponseStatus;
import in.zeko.enums.StepCountWebServiceRequestType;
import in.zeko.utils.Phase;

public class RequestStat {

  public StepCountWebServiceRequestType requestType;
  public ResponseStatus responseStatus;
  public long latency;
  public String timestamp;
  public String phase;

  public RequestStat(StepCountWebServiceRequestType requestType, ResponseStatus responseStatus, long latency, String phase) {
    this.requestType = requestType;
    this.responseStatus = responseStatus;
    this.latency = latency;
    this.timestamp = new Date().toString();
    this.phase = phase;
  }

  @Override
  public String toString() {
    return this.phase + "," + this.requestType + "," + this.responseStatus + "," + this.timestamp + "," + this.latency;
  }

  public static RequestStat parse(String csvLine) {
    String[] parts = csvLine.split(",");
    RequestStat stat = new RequestStat(StepCountWebServiceRequestType.parse(parts[1]), ResponseStatus.parse(parts[2]), Long.parseLong(parts[4]), parts[0]);
    stat.timestamp = parts[3];

    return stat;
  }
}
