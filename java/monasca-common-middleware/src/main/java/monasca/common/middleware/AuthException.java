package monasca.common.middleware;

/**
 * An exception to indicate any authentication error.
 * 
 * @author liemmn
 *
 */
public class AuthException extends RuntimeException {

  private static final long serialVersionUID = 5860956829821067827L;

  public AuthException(String msg) {
		super(msg);
	}
	
	public AuthException(String msg, Exception e) {
		super(msg, e);
	}
}
