package monasca.common.middleware;

/**
 * Created by johnderr on 6/25/14.
 */
public class UnavailableException extends RuntimeException {

  private static final long serialVersionUID = -2353922744077869466L;

  public UnavailableException(String msg) {
    super(msg);
  }

  public UnavailableException(String msg, Exception e) {
    super(msg, e);
  }
}
