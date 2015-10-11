package org.openmrs.module.webservices.rest.web.v1_0.converter.openmrs2_0;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.response.ConversionException;

/**
 * Helper base class if you want a Converter that can only produce a representation for output. This throws an exception
 * on all operations that have to do with reading in input to create object.
 * @param <T>
 */
public abstract class OutputOnlyConverter2_0<T> implements Converter<T> {

    @Override
    public T newInstance(String s) {
        throw new IllegalStateException();
    }

    @Override
    public T getByUniqueId(String s) {
        throw new IllegalStateException();
    }

    @Override
    public Object getProperty(T t, String s) throws ConversionException {
        try {
            return PropertyUtils.getProperty(t, s);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void setProperty(Object o, String s, Object o1) throws ConversionException {
        throw new IllegalStateException();
    }
}
