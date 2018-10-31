package in.zeko.enums;

public enum StepCountWebServiceRequestType {

  GET_CURRENT_STEP_COUNT("get:current"),
  GET_SINGLE_DAY_STEP_COUNT("get:single"),
  POST_STEP_COUNT("post:count");

  private String tag;

  StepCountWebServiceRequestType(String str) {
    this.tag = str;
  }

  @Override
  public String toString() {
    return tag;
  }

  public static StepCountWebServiceRequestType parse(String str) {

    switch (str) {
      case "get:current":
        return GET_CURRENT_STEP_COUNT;
      case "get:single":
        return GET_SINGLE_DAY_STEP_COUNT;
      case "post:count":
        return POST_STEP_COUNT;
    }

    return null;
  }
}
