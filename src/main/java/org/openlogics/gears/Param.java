/*
 *     gears
 *     http://www.open-logics.com
 *     Copyright (C) 2012, OpenLogics
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlogics.gears;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.UIManager;

/**
 * Data class used to capture Parameter requirements.
 *
 * <p> Subclasses may provide specific setAsText()/getAsText() requirements </p>
 *
 * <p> Warning: We would like to start moving towards a common parameters
 * framework with GridCoverageExchnage. Param will be maintained as a wrapper
 * for one point release (at which time it will be deprecated). </p>
 *
 * @author Miguel Vega
 * @version $Id: Param.java 2012-07-20 4:47:04 PM mvega $
 */
@SuppressWarnings("unchecked")
public class Param <T>implements Serializable{
    private Class type;
    private String description, key;
    private boolean required;
    private T sample;
    private Map<String, ?> metadata;
    public static void main(String[] args) {
        UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo laf : lafInfo) {
            System.out.println("LAF:"+laf.getName()+"::"+laf.getClassName());
        }
    }
    /**
     * Provides support for text representations
     *
     * <p> The parameter type of String is assumed. </p>
     *
     * @param key Key used to file this Param in the Parameter Map for
     * createDataStore
     */
    public Param(String key) {
        this(key, String.class, null);
    }

    public Param(String key, T sample) {
        this(key, sample!=null?sample.getClass():String.class, null, true, sample);
    }

    /**
     * Provides support for text representations.
     *
     * <p> You may specify a
     * <code>type</code> for this Param. </p>
     *
     * @param key Key used to file this Param in the Parameter Map for
     * createDataStore
     * @param type Class type intended for this Param
     */
    public Param(String key, Class<?> type) {
        this(key, type, null);
    }

    /**
     * Provides support for text representations
     *
     * @param key Key used to file this Param in the Parameter Map for
     * createDataStore
     * @param type Class type intended for this Param
     * @param description User description of Param (40 chars or less)
     */
    public Param(String key, Class<?> type, String description) {
        this(key, type, description, true);
    }

    /**
     * Provides support for text representations
     *
     * @param key Key used to file this Param in the Parameter Map for
     * createDataStore
     * @param type Class type intended for this Param
     * @param description User description of Param (40 chars or less)
     * @param required
     * <code>true</code> is param is required
     */
    public Param(String key, Class<?> type, String description, boolean required) {
        this(key, type, description, required, null);
    }

    /**
     * Provides support for text representations
     *
     * @param key Key used to file this Param in the Parameter Map for
     * createDataStore
     * @param type Class type intended for this Param
     * @param description User description of Param (40 chars or less)
     * @param required
     * <code>true</code> is param is required
     * @param sample Sample value as an example for user input
     */
    public Param(String key, Class<?> type, String description, boolean required, T sample) {
        this(key, type, description, required, sample, null);
    }

    /**
     * Provides support for text representations
     *
     * @param key Key used to file this Param in the Parameter Map for
     * createDataStore
     * @param type Class type intended for this Param
     * @param description User description of Param (40 chars or less)
     * @param required
     * <code>true</code> is param is required
     * @param sample Sample value as an example for user input
     * @param metadata metadata information, preferably keyed by known identifiers
     *
     */
    public Param(String key,
                 Class type,
                 String description,
                 boolean required,
                 T sample,
                 Map<String, ?> metadata) {
        this.key = key;
        this.description = description;
        this.metadata = metadata;
        this.required = required;
        this.sample = sample;
        this.type = type;
    }

    public String getKey(){
        return key;
    }
    public T getSample(){
        return sample;
    }

    /**
     * Lookup Param in a user supplied map.
     *
     * <p> Type conversion will occur if required, this may result in an
     * IOException. An IOException will be throw in the Param is required and
     * the Map does not contain the Map. </p>
     *
     * <p> The handle method is used to process the user's value. </p>
     *
     * @param map Map of user input
     *
     * @return Parameter as specified in map
     *
     * @throws IOException if parse could not handle value
     */
    public Object lookUp(Map<String, ?> map) throws IOException {
        if (!map.containsKey(key)) {
            if (required) {
                throw new IOException("Parameter " + key + " is required:" + description);
            } else {
                return null;
            }
        }

        Object value = map.get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof String && (type != String.class)) {
            value = handle((String) value);
        }

        if (value == null) {
            return null;
        }

        if (!type.isInstance(value)) {
            throw new IOException(type.getName() + " required for parameter " + key + ": not "
                    + value.getClass().getName());
        }

        return value;
    }

    /**
     * Convert value to text representation for this Parameter
     *
     * @param value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String text(Object value) {
        return value.toString();
    }

    /**
     * Handle text in a sensible manner.
     *
     * <p> Performs the most common way of handling text value: </p>
     *
     * <ul> <li> null: If text is null </li> <li> origional text: if type ==
     * String.class </li> <li> null: if type != String.class and text.getLength
     * == 0 </li> <li> parse( text ): if type != String.class </li> </ul>
     *
     *
     * @param text
     *
     * @return Value as processed by text
     *
     * @throws IOException If text could not be parsed
     */
    public Object handle(String text) throws IOException {
        if (text == null) {
            return null;
        }

        if (type == String.class) {
            return text;
        }

        if (text.length() == 0) {
            return null;
        }

        // if type is an array, tokenize the string and have the reflection
        // parsing be tried on each element, then build the array as a result
        if (type.isArray()) {
            StringTokenizer tokenizer = new StringTokenizer(text, " ");
            List<Object> result = new ArrayList<Object>();

            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                Object element;

                try {
                    if (type.getComponentType() == String.class) {
                        element = token;
                    } else {
                        element = parse(token);
                    }
                } catch (IOException ioException) {
                    throw ioException;
                } catch (Throwable throwable) {
                    throw new IOException("Problem creating " + type.getName()
                            + " from '" + text + "'", throwable);
                }

                result.add(element);
            }

            Object array = Array.newInstance(type.getComponentType(), result.size());

            for (int i = 0; i < result.size(); i++) {
                Array.set(array, i, result.get(i));
            }

            return array;
        }

        try {
            return parse(text);
        } catch (IOException ioException) {
            throw ioException;
        } catch (Throwable throwable) {
            throw new IOException("Problem creating " + type.getName() + " from '"
                    + text + "'", throwable);
        }
    }

    /**
     * Provides support for text representations
     *
     * <p> Provides basic support for common types using reflection. </p>
     *
     * <p> If needed you may extend this class to handle your own custome types.
     * </p>
     *
     * @param text Text representation of type should not be null or empty
     *
     * @return Object converted from text representation
     *
     * @throws Throwable DOCUMENT ME!
     * @throws IOException If text could not be parsed
     */
    public Object parse(String text) throws Throwable {
        Constructor<?> constructor;

        try {
            constructor = type.getConstructor(new Class[]{String.class});
        } catch (SecurityException e) {
            //  type( String ) constructor is not public
            throw new IOException("Could not create " + type.getName() + " from text");
        } catch (NoSuchMethodException e) {
            // No type( String ) constructor
            throw new IOException("Could not create " + type.getName() + " from text");
        }

        try {
            return constructor.newInstance(new Object[]{text,});
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new Exception("Could not create " + type.getName() + ": from '"
                    + text + "'", illegalArgumentException);
        } catch (InstantiationException instantiaionException) {
            throw new Exception("Could not create " + type.getName() + ": from '"
                    + text + "'", instantiaionException);
        } catch (IllegalAccessException illegalAccessException) {
            throw new Exception("Could not create " + type.getName() + ": from '"
                    + text + "'", illegalAccessException);
        } catch (InvocationTargetException targetException) {
            throw targetException.getCause();
        }
    }

    /**
     * key=Type description
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(key);
        buf.append('=');
        buf.append(type.getName());
        buf.append(' ');

        if (required) {
            buf.append("REQUIRED ");
        }

        buf.append(description);

        return buf.toString();
    }
}