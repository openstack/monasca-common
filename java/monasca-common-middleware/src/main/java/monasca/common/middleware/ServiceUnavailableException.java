package monasca.common.middleware;

/**
 * Created by johnderr on 6/25/14.
 */
public class ServiceUnavailableException extends RuntimeException {

  private static final long serialVersionUID = -2353922744077869466L;

  public ServiceUnavailableException(String msg) {
    super(msg);
  }

  public ServiceUnavailableException(String msg, Exception e) {
    super(msg, e);
  }
}
