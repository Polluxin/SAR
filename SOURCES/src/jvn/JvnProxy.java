package jvn;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JvnProxy implements InvocationHandler {

    private JvnObject jo;

    public JvnProxy(Object o, String n){
        try {
            JvnServerImpl js = JvnServerImpl.jvnGetServer();
            JvnObject jvnObject = js.jvnLookupObject(n);
            if (jvnObject == null) {
                jvnObject = js.jvnCreateObject( (Serializable) o);
                js.jvnRegisterObject(n, jvnObject);
            }
            jo = jvnObject;
        } catch (Exception e){

            e.printStackTrace();
        }
    }

    public static Object newInstance(Object o, String name){
        return Proxy.newProxyInstance(
                o.getClass().getClassLoader(),
                o.getClass().getInterfaces(),
                new JvnProxy(o, name)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        JvnAnnotation n = method.getAnnotation(JvnAnnotation.class);

        if (n == null) {
            System.out.println("Call to unannotated method");
            return method.invoke(jo.jvnGetSharedObject(), args);
        }
        else if (n.name() == JvnAnnotationType.WRITE){
            jo.jvnLockWrite();
        }
        else if (n.name() == JvnAnnotationType.READ){
            jo.jvnLockRead();
        }
        result = method.invoke(jo.jvnGetSharedObject(), args);
        jo.jvnUnLock();
        return result;
    }
}
