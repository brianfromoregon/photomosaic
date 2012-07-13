/**
 *
 */
package org.jboss.resteasy.util;

import org.jboss.resteasy.core.ExceptionAdapter;
import org.jboss.resteasy.logging.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * I'm monkey patching to get around error: java.lang.ClassCastException: Cannot cast java.lang.Boolean to boolean
 *
 * A utility class that can convert a String value as a typed object.
 *
 * @author <a href="ryan@damnhandy.com>Ryan J. McDonough</a>
 * @version $Revision: $
 */
public final class TypeConverter
{
   private static final String VALUE_OF_METHOD = "valueOf";

   private static final Logger logger = Logger.getLogger(TypeConverter.class);

   /**
    * A map of primitive to objects.
    */
   private static final Map<Class<?>, Class<?>> PRIMITIVES;

   static
   {
      PRIMITIVES = new HashMap<Class<?>, Class<?>>();
      PRIMITIVES.put(int.class, Integer.class);
      PRIMITIVES.put(double.class, Double.class);
      PRIMITIVES.put(float.class, Float.class);
      PRIMITIVES.put(short.class, Short.class);
      PRIMITIVES.put(byte.class, Byte.class);
      PRIMITIVES.put(long.class, Long.class);
   }

   private TypeConverter()
   {

   }

   /**
    * A generic method that returns the {@link String} as the specified Java type.
    *
    * @param <T>        the type to return
    * @param source     the string value to convert
    * @param targetType
    * @return the object instance
    */
   public static <T> T getType(final Class<T> targetType, final String source)
   {
      // just return that source if it's a String
      if (String.class.equals(targetType))
      {
         return targetType.cast(source);
      }
      /*
       * Dates are too complicated for this class.
       */
      if (Date.class.isAssignableFrom(targetType))
      {
         throw new IllegalArgumentException("Date instances are not supported by this class.");
      }
      T result;
      // boolean types need special handling
      if (Boolean.class.equals(targetType) || boolean.class.equals(targetType))
      {
          return (T) getBooleanValue(source);
      }
      try
      {
         result = getTypeViaValueOfMethod(source, targetType);
      }
      catch (NoSuchMethodException e)
      {
         logger.warn("No valueOf() method available for {0}, trying constructor...", targetType
                 .getSimpleName());
         result = getTypeViaStringConstructor(source, targetType);
      }
      return result;
   }

   /**
    * Tests if the class can safely be converted from a String to the
    * specified type.
    *
    * @param targetType the type to convert to
    * @return true if the class possesses either a "valueOf()" method or a constructor with a String
    *         parameter.
    */
   public static boolean isConvertable(final Class<?> targetType)
   {
      if (Boolean.class.equals(targetType))
      {
         return true;
      }
      if (targetType.isPrimitive())
      {
         return true;
      }
      try
      {
         targetType.getDeclaredMethod(VALUE_OF_METHOD, String.class);
         return true;
      }
      catch (NoSuchMethodException e)
      {
         try
         {
            targetType.getDeclaredConstructor(String.class);
            return true;
         }

         catch (NoSuchMethodException e1)
         {
            return false;
         }
      }
   }

   /**
    * <p>
    * Returns a Boolean value from a String. Unlike {@link Boolean#valueOf(String)}, this
    * method takes more String options. The following String values will return true:
    * </p>
    * <ul>
    * <li>Yes</li>
    * <li>Y</li>
    * <li>T</li>
    * <li>1</li>
    * </ul>
    * <p>
    * While the following values will return false:
    * </p>
    * <ul>
    * <li>No</li>
    * <li>N</li>
    * <li>F</li>
    * <li>0</li>
    * </ul>
    *
    * @param source
    * @return
    */
   public static Boolean getBooleanValue(final String source)
   {
      if ("Y".equalsIgnoreCase(source) || "T".equalsIgnoreCase(source)
              || "Yes".equalsIgnoreCase(source) || "1".equalsIgnoreCase(source))
      {
         return Boolean.TRUE;
      }
      else if ("N".equals(source) || "F".equals(source) || "No".equals(source)
              || "0".equalsIgnoreCase(source))
      {
         return Boolean.FALSE;
      }
      return Boolean.valueOf(source);
   }

   /**
    * @param <T>
    * @param source
    * @param targetType
    * @return
    * @throws NoSuchMethodException
    */
   @SuppressWarnings("unchecked")
   public static <T> T getTypeViaValueOfMethod(final String source, final Class<T> targetType)
           throws NoSuchMethodException
   {
      Class<?> actualTarget = targetType;
      /*
       * if this is a primitive type, use the Object class's "valueOf()" 
       * method.
       */
      if (targetType.isPrimitive())
      {
         actualTarget = PRIMITIVES.get(targetType);
      }
      T result = null;
      try
      {
         // if the type has a static "valueOf()" method, try and create the instance that way
         Method valueOf = actualTarget.getDeclaredMethod(VALUE_OF_METHOD, String.class);
         Object value = valueOf.invoke(null, source);
         if (actualTarget.equals(targetType) && targetType.isInstance(value))
         {
            result = targetType.cast(value);
         }
         /*
          * handle the primitive case
          */
         else if (!actualTarget.equals(targetType) && actualTarget.isInstance(value))
         {
            // because you can't use targetType.cast() with primitives.
            result = (T) value;
         }
      }
      catch (IllegalAccessException e)
      {
         throw new ExceptionAdapter(e);
      }
      catch (InvocationTargetException e)
      {
         throw new ExceptionAdapter(e);
      }
      return result;
   }

   /**
    * @param <T>
    * @param source
    * @param targetType
    * @return
    * @throws IllegalArgumentException
    * @throws InstantiationException
    * @throws IllegalAccessException
    * @throws java.lang.reflect.InvocationTargetException
    */
   private static <T> T getTypeViaStringConstructor(String source, Class<T> targetType)
   {
      T result = null;
      Constructor<T> c = null;

      try
      {
         c = targetType.getDeclaredConstructor(String.class);
      }
      catch (NoSuchMethodException e)
      {
         String msg = new StringBuilder().append(targetType.getName()).append(
                 " has no String constructor").toString();
         throw new IllegalArgumentException(msg, e);
      }

      try
      {
         result = c.newInstance(source);
      }
      catch (InstantiationException e)
      {
         throw new ExceptionAdapter(e);
      }
      catch (IllegalAccessException e)
      {
         throw new ExceptionAdapter(e);
      }
      catch (InvocationTargetException e)
      {
         throw new ExceptionAdapter(e);
      }
      return result;
   }
}
