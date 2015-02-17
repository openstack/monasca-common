package monasca.common.middleware;


import org.apache.http.client.ClientProtocolException;
import org.apache.thrift.TException;

/**
 * A client that can communicate to an authentication server for authentication.
 *
 * @author liemmn
 */
public interface AuthClient {

  public String validateTokenForServiceEndpointV3(String token) throws TException, ClientProtocolException;
}
