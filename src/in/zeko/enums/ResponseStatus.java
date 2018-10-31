package in.zeko.enums;

public enum ResponseStatus {
  SUCCESSFUL("success"),
  FAILED("failed");

  private String tag;

  ResponseStatus(String str) {
    this.tag = str;
  }

  @Override
  public String toString() {
    return tag;
  }

  public static ResponseStatus parse(String str) {
    switch (str) {
      case "success":
        return SUCCESSFUL;
      case "failed":
        return FAILED;
    }
    return null;
  }
}
