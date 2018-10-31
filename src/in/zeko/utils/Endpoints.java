/**
 * @author yashchoukse
 */

package in.zeko.utils;

import java.util.HashMap;
import java.util.Map;

public class Endpoints {

  private Map<String, String> data;

  public Endpoints() {
    data = new HashMap<String, String>();
  }

  public void addEndpoint(String tag, String endpoint) {
    data.put(tag, endpoint);
  }

  public String getEndpoint(String tag) {
    return data.get(tag);
  }
}
