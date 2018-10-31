package in.zeko.jobs;

import in.zeko.utils.Endpoints;

public class WebServiceJobMeta {

  private String serverIp;

  private int applicationPort;
  private Endpoints endpoints;

  public WebServiceJobMeta(String serverIp, int applicationPort, Endpoints endpoints) {
    this.serverIp         = serverIp;
    this.applicationPort  = applicationPort;
    this.endpoints         = endpoints;
  }

  public String getServerIp() {
    return serverIp;
  }

  public int getApplicationPort() {
    return applicationPort;
  }

  public Endpoints getEndpoint() {
    return endpoints;
  }
}
