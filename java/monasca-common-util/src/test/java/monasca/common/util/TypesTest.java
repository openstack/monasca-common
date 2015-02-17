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
package monasca.common.util;

import static com.google.inject.matcher.Matchers.any;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;

import javassist.util.proxy.ProxyFactory;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;

@Test
public class TypesTest {
  static class TestClass {
  }

  static class TestInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation arg0) throws Throwable {
      return null;
    }
  }

  public void shouldDeProxyCGLibProxy() throws Exception {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(ArrayList.class);
    enhancer.setCallbackTypes(new Class[] { NoOp.class });
    Class<?> proxy = enhancer.createClass();

    assertEquals(Types.deProxy(proxy), ArrayList.class);
  }

  public void shouldDeProxyGuiceEnhancedProxy() {
    Injector.registerModules(new AbstractModule() {
      @Override
      protected void configure() {
        bind(TestClass.class);
        bindInterceptor(any(), any(), new TestInterceptor());
      }
    });

    assertEquals(Types.deProxy(Injector.getInstance(TestClass.class).getClass()), TestClass.class);
  }

  @Test(enabled = false)
  public void shouldDeProxyJavassistProxy() {
    ProxyFactory proxyFactory = new ProxyFactory();
    proxyFactory.setSuperclass(ArrayList.class);
    Class<?> proxy = proxyFactory.createClass();

    assertEquals(Types.deProxy(proxy), ArrayList.class);
  }
}
