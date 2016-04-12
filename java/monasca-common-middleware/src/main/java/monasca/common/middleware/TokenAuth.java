/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package monasca.common.middleware;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.util.concurrent.UncheckedExecutionException;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A token-based authentication filter. This filter uses Thrift protocol to
 * communicate with the CS server. The token to validate is set via the header
 * {@link #TOKEN}.
 * <p/>
 * A token is required to validate. However, if no token is presented, the
 * filter will set the {@link #AUTH_IDENTITY_STATUS} request parameter to
 * <code>Invalid</code> and let any other filter downstream to decide what to
 * do. For instance, if a downstream filter knows how to deal with signature
 * rather than tokens, then it will go ahead and validate with signatures.
 * <p/>
 * Upon successful validation, all the Auth request parameters will be
 * populated, including information such as tenant, user and user roles, and
 * passed down to the next filter downstream.
 * <p/>
 * Upon unsuccessful validation, this filter will terminate the request by
 * returning a 401 (unauthorized).
 *
 * @author liemmn
 */
public class TokenAuth implements Filter, monasca.common.middleware.AuthConstants {

  private static final String TOKEN_NOTFOUND = "Bad Request: Token not found in the request";

  private final monasca.common.middleware.Config appConfig = Config.getInstance();

  private FilterConfig filterConfig;

  // Thee faithful logger
  private static final Logger logger = LoggerFactory
          .getLogger(TokenAuth.class);

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    this.filterConfig = filterConfig;
  }


  /**
   * {@inheritDoc}
   */
  public void destroy() {
    FilterUtils.destroyFilter();
  }

  /**
   * {@inheritDoc}
   */
  public void doFilter(ServletRequest req, ServletResponse resp,
                       FilterChain chain) throws IOException, ServletException {
    // According to CORS spec OPTIONS method does not pass auth info
    if (((HttpServletRequest) req).getMethod().equals("OPTIONS")) {
      chain.doFilter(req, resp);
      return;
    }
    Object auth = null;
    int numberOfTries = 0;
    if (!appConfig.isInitialized()) {
      appConfig.initialize(filterConfig, req);
    }
    int retries = appConfig.getRetries();
    long pauseTime = appConfig.getPauseTime();

    // Extract credential
    String token = ((HttpServletRequest) req).getHeader(TOKEN);

    if (token == null) {
      if (!appConfig.isDelayAuthDecision()) {
        logger.debug(HttpServletResponse.SC_UNAUTHORIZED
                + " No token found.");
        ((HttpServletResponse) resp).sendError(
                HttpServletResponse.SC_UNAUTHORIZED, TOKEN_NOTFOUND);
        return;
      } else {
        logger.info("No token found...Skipping");
      }
    } else {
      do {
        try {
          auth = FilterUtils.getCachedToken(token);
        } catch (ServiceUnavailableException e) {
          if (numberOfTries < retries) {
            FilterUtils.pause(pauseTime);
            logger.debug("Retrying connection after "
                    + pauseTime + " seconds.");
            numberOfTries++;
            continue;
          } else {
            logger.debug("Exhausted retries..");
            TokenExceptionHandler handler = TokenExceptionHandler
                    .valueOf("ServiceUnavailableException");
            handler.onException(e, resp, token);
          }
          return;
        } catch (ClientProtocolException e) {
          if (numberOfTries < retries) {
            FilterUtils.pause(pauseTime);
            logger.debug("Retrying connection after "
                    + pauseTime + " seconds.");
            numberOfTries++;
            continue;
          } else {
            logger.debug("Exhausted retries..");
            TokenExceptionHandler handler = TokenExceptionHandler
                    .valueOf("ClientProtocolException");
            handler.onException(e, resp, token);
          }
          return;
        } catch (UncheckedExecutionException e) {
          final TokenExceptionHandler handler;
          final Exception toHandle;
          if ((e.getCause() != null) && e.getCause() instanceof AdminAuthException) {
            toHandle = (AdminAuthException)e.getCause();
            handler = TokenExceptionHandler.valueOf("AdminAuthException");
          }
          else if ((e.getCause() != null) && e.getCause() instanceof AuthException) {
            toHandle = (AuthException)e.getCause();
            handler = TokenExceptionHandler.valueOf("AuthException");
          }
          else {
            toHandle = e;
            handler = TokenExceptionHandler.valueOf("UncheckedExecutionException");
          }
          handler.onException(toHandle, resp, token);
          return;
        }


      } while (auth == null && numberOfTries <= retries);
    }
    req = FilterUtils.wrapRequest(req, auth);
    logger.debug("TokenAuth: Forwarding down stream to next filter/servlet");
    // Forward downstream...
    chain.doFilter(req, resp);
  }
}
