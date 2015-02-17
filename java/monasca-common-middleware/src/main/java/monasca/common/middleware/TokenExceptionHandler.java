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

import org.apache.http.client.ClientProtocolException;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.thrift.TException;
import com.google.common.util.concurrent.UncheckedExecutionException;

public enum TokenExceptionHandler {

  AuthConnectionException {
    @Override
    public void onException(Exception e, ServletResponse resp, String token) {
      AuthConnectionException ae = (AuthConnectionException) e;
      logger.error(ae.getMessage() + " " + ae);
      try {
        ((HttpServletResponse) resp).sendError(
          HttpServletResponse.SC_UNAUTHORIZED,
          ExceptionHandlerUtil.getStatusText(HttpServletResponse.SC_UNAUTHORIZED)
            + " " + token);
      } catch (IOException ie) {
        logger.debug("Error in writing the HTTP response "
          + ie.getMessage() + " " + ie);
      }
    }
  },
  TException {
    @Override
    public void onException(Exception e, ServletResponse resp, String token) {
      TException t = (TException) e;
      logger.error("Thrift Exception " + t.getMessage() + " " + t);
      try {
        ((HttpServletResponse) resp).sendError(
          HttpServletResponse.SC_UNAUTHORIZED,
          ExceptionHandlerUtil.getStatusText(HttpServletResponse.SC_UNAUTHORIZED)
            + " " + token);
      } catch (IOException ie) {
        logger.debug("Error in writing the HTTP response "
          + ie.getMessage() + " " + ie);
      }
    }
  },
  ClientProtocolException {
    @Override
    public void onException(Exception e, ServletResponse resp, String token) {
      ClientProtocolException t = (ClientProtocolException) e;
      logger.error("Http Client Exception " + t.getMessage() + " " + t);
      try {
        ((HttpServletResponse) resp).sendError(
          HttpServletResponse.SC_UNAUTHORIZED,
          ExceptionHandlerUtil.getStatusText(HttpServletResponse.SC_UNAUTHORIZED)
            + " " + token);
      } catch (IOException ie) {
        logger.debug("Error in writing the HTTP response "
          + ie.getMessage() + " " + ie);
      }
    }
  },UncheckedExecutionException {
    @Override
    public void onException(Exception e, ServletResponse resp, String token) {
      UncheckedExecutionException t = (UncheckedExecutionException) e;
      logger.error("Http Client Exception " + t.getMessage() + " " + t);
      try {
        ((HttpServletResponse) resp).sendError(
          HttpServletResponse.SC_UNAUTHORIZED,
          ExceptionHandlerUtil.getStatusText(HttpServletResponse.SC_UNAUTHORIZED)
            + " " + token);
      } catch (IOException ie) {
        logger.debug("Error in writing the HTTP response "
          + ie.getMessage() + " " + ie);
      }
    }
  }
  ,
  AdminAuthException {
    @Override
    public void onException(Exception e, ServletResponse resp, String token) {
      AdminAuthException ae = (AdminAuthException) e;
      logger.error(ae.getMessage() + " " + ae);
      // Don't want to send any information about the admin auth to clients
      String statusText = ExceptionHandlerUtil.getStatusText(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      try {
        ((HttpServletResponse) resp).sendError(
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          statusText);
      } catch (IOException ie) {
        logger.debug("Error in writing the HTTP response "
          + ie.getMessage() + " " + ie);
      }
    }
  }
  ,
  AuthException {
    @Override
    public void onException(Exception e, ServletResponse resp, String token) {
      AuthException ae = (AuthException) e;
      logger.info(ae.getMessage() + " " + ae);
      String statusText = ae.getMessage();
      if (statusText == null || statusText.isEmpty()) {
        statusText = ExceptionHandlerUtil.getStatusText(HttpServletResponse.SC_UNAUTHORIZED);
      }
      try {
        ((HttpServletResponse) resp).sendError(
          HttpServletResponse.SC_UNAUTHORIZED,
          statusText + " " + token);
      } catch (IOException ie) {
        logger.debug("Error in writing the HTTP response "
          + ie.getMessage() + " " + ie);
      }
    }
  }, ServiceUnavailableException {
    @Override
    public void onException(Exception e, ServletResponse resp, String token) {
      ServiceUnavailableException ae = (ServiceUnavailableException) e;
      logger.error(ae.getMessage() + " " + ae);
      String statusText = ae.getMessage();
      if (statusText == null || statusText.isEmpty()) {
        statusText = ExceptionHandlerUtil.getStatusText(HttpServletResponse.SC_UNAUTHORIZED);
      }
      try {
        ((HttpServletResponse) resp).sendError(
          HttpServletResponse.SC_UNAUTHORIZED,
          statusText + " " + token);
      } catch (IOException ie) {
        logger.debug("Error in writing the HTTP response "
          + ie.getMessage() + " " + ie);
      }
    }
  };

  final Logger logger = LoggerFactory.getLogger(TokenExceptionHandler.class);

  abstract void onException(Exception e, ServletResponse resp, String token);
}
