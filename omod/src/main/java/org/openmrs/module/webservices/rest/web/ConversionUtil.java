package org.openmrs.module.webservices.rest.web;

import java.beans.PropertyDescriptor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.DelegateConverter;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.util.HandlerUtil;


public class ConversionUtil {

	static final Log log = LogFactory.getLog(ConversionUtil.class);
	
	/**
	 * Sets all the given properties on the bean, converting them to resources as necessary.
	 * @param bean
	 * @param properties
	 */
	public static void setConvertedProperties(Object bean, Map<String, Object> propertyMap) throws ConversionException {
		for (Map.Entry<String, Object> prop : propertyMap.entrySet()) {
    		setConvertedProperty(bean, prop.getKey(), prop.getValue());
    	}
	}
	
	/**
	 * Sets the given property on the bean, converting types as necessary
	 * @param property
	 * @param value
	 * @throws ConversionException 
	 */
	public static void setConvertedProperty(Object bean, String property, Object value) throws ConversionException {
		try {
		    log.trace("applying " + property + " which is a " + value.getClass() + " = " + value);
		    PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(bean, property);
		    log.trace("property exists and is a: " + pd.getPropertyType());
		    if (value == null || pd.getPropertyType().isAssignableFrom(value.getClass())) {
		    	log.trace("compatible type, so setting directly");
		    	pd.getWriteMethod().invoke(bean, value);
		    } else {
		    	log.trace("need to convert " + value.getClass() + " to " + pd.getPropertyType());
		    	Object converted = convert(value, pd.getPropertyType());
		    	pd.getWriteMethod().invoke(bean, converted);
		    }
		} catch (Exception ex) {
			throw new ConversionException("setting " + property + " on " + bean.getClass(), ex);
		}
    }

	/**
	 * Converts the given object to the given type
	 * @param object
	 * @param toType
	 * @return
	 * @throws ConversionException 
	 */
	public static Object convert(Object object, Class<?> toType) throws ConversionException {
		if (object instanceof String) {
			String string = (String) object;
			DelegateConverter<?> converter = null;
			try {
				converter = HandlerUtil.getPreferredHandler(DelegateConverter.class, toType);
				return converter.getByUniqueId(string);
			} catch (APIException ex) {
				// it's okay if there's no registered handler
			}

			if (toType.isAssignableFrom(Date.class)) {
				try {
					return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(string);
				} catch (ParseException ex) {
					try {
						return new SimpleDateFormat("yyyy-MM-dd").parse(string);
					} catch (ParseException ex2) {
						throw new ConversionException("converting date", ex);
					}
				}
			}
		}
	    throw new ConversionException("Don't know how to convert from " + object.getClass() + " to " + toType, null);
    }
	
	/**
	 * Gets a property from the delegate, with the given representation
	 * @param propertyName
	 * @param rep
	 * @return
	 * @throws ConversionException
	 */
	public static Object getPropertyWithRepresentation(Object bean, String propertyName, Representation rep) throws ConversionException {
		Object o;
		try {
			o = PropertyUtils.getProperty(bean, propertyName);
		} catch (Exception ex) {
			throw new ConversionException(null, ex);
		}
		if (o instanceof Collection) {
			List<Object> ret = new ArrayList<Object>();
			for (Object element : (Collection<?>) o)
				ret.add(convertToRepresentation(element, rep));
			return ret;
		} else {
			o = convertToRepresentation(o, rep);
			return o;
		}
    }
	
	@SuppressWarnings("unchecked")
    public static <S> Object convertToRepresentation(S o, Representation rep) throws ConversionException {
		if (o == null)
			return null;
		DelegateConverter<S> converter = null;
		try {
			converter = HandlerUtil.getPreferredHandler(DelegateConverter.class, o.getClass());
		} catch (APIException ex) {
			// try a few known datatypes
			if (o instanceof Date) {
				return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format((Date) o);
			}
			// otherwise we have no choice but to return the plain object
			return o;
		}
		try {
			converter = converter.getClass().newInstance();
			return converter.asRepresentation(o, rep);
		} catch (Exception ex) {
			throw new ConversionException("converting " + o.getClass() + " to " + rep, ex);
		}
	}


}
