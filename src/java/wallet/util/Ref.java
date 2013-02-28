package wallet.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

public class Ref {
	
	private static Logger logger = Logger.getLogger(Str.class);
	
	private Ref() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Calls an arbitrary method 
	 */
	public static <T> T callMethod(String name, Object instance, Class<?> [] parameterTypes, Object[] paramsValues) {
		Fix.require((name = Str.nil(name)) != null, instance != null, "name", "instance");
		try {
			Class<?> c = null;
			Method m = null;
			
			// Try to find a public method
			c = instance.getClass();
			try {
				m = c.getMethod(name, parameterTypes);
				@SuppressWarnings("unchecked")
				T result = (T) m.invoke(instance, paramsValues); 
				return result;									
			} catch (NoSuchMethodException e) {
				// ok
			} 
			
			// Try to find a method with different access (search only in super classes, of course)	
			do {
				c = instance.getClass();				
				try {
					m = c.getDeclaredMethod(name, parameterTypes);
					m.setAccessible(true);
					@SuppressWarnings("unchecked")
					T result = (T) m.invoke(instance, paramsValues); 
					return result;					
				} catch (NoSuchMethodException e) {
					// ok
				}
			} while (! (c == null || Object.class.equals(c)));
			throw new NoSuchMethodException(name);
		} catch (NoSuchMethodException e ) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}		
	}

}
