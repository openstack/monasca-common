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

public class ExceptionHandlerUtil {
	
	public final static String SERVICE_UNAVAILABLE = "Service Unavailable";
	public final static String UNAUTHORIZED_TOKEN = "Unauthorized Token";
	public final static String INTERNAL_SERVER_ERROR = "Internal Server Error";
	
	private ExceptionHandlerUtil() {	
	}
	
	public static String getStatusText(int errorCode) {
		if (errorCode == 401) {
			return UNAUTHORIZED_TOKEN;
		}
		if (errorCode == 503) {
			return SERVICE_UNAVAILABLE;
		}
		if (errorCode == 500) {
			return INTERNAL_SERVER_ERROR;
		}
		return "Unknown Error";

	}
	
	public static TokenExceptionHandler lookUpTokenException(Exception ex) {
		try {
			return TokenExceptionHandler.valueOf(ex.getClass().getSimpleName());
		} catch (IllegalArgumentException iae) {
			return TokenExceptionHandler.valueOf("ResourceException");
		}
	}

}
