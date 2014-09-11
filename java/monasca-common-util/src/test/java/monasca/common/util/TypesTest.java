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
