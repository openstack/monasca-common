package monasca.common.middleware;

/**
 * An exception to indicate authentication error on the Admin
 *
 */
public class AdminAuthException extends RuntimeException {

  private static final long serialVersionUID = 9192863663767343364L;

  public AdminAuthException(String msg) {
        super(msg);
    }

    public AdminAuthException(String msg, Exception e) {
        super(msg, e);
    }
}
