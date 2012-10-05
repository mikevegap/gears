/*
 *     JavaTools
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

package org.openlogics.gears.text;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Dictionary;
import java.util.Map;

/**
 * @author Miguel Vega
 * @version $Id: ParamStringELvaluator.java 0, 2012-09-29 11:22 mvega $
 */
public class ParamStringELvaluator implements ELEvaluator<Object, Object> {
    static final String ATTRIBUTE_SEPARATOR = "\\.";

    /*public Object evaluate(InputStream inputStream, Object context) {
        if (isPrimitive(context)) {
            return context;
        }

        final Scanner scanner = new Scanner(inputStream);
        scanner.useDelimiter(ATTRIBUTE_SEPARATOR);

        final String token = scanner.next();

        if (scanner==null) {
            //simply return context
            return context;
        } else if (!scanner.hasNext()) {
            return getFinalValue(context, token);
        } else {
            int offset = token.length() + 1;
            //make retrieved value the new context
            return evaluate(inputStream, getFinalValue(context, token));
        }
    }*/

    @Override
    public Object evaluate(String textToEvaluate, Object context) {
        final String[] tokens = textToEvaluate.split(ATTRIBUTE_SEPARATOR);
        if (tokens.length==0) {
            //simply return context
            return getFinalValue(context, textToEvaluate);
        } else if (tokens.length == 1) {
            return getFinalValue(context, tokens[0]);
        } else {
            String s = tokens[0];
            int offset = s.length() + 1;
            //make retrieved value the new context
            return evaluate(textToEvaluate.substring(offset, textToEvaluate.length()), getFinalValue(context, s));
        }
    }

    private Object getFinalValue(Object context, String attr) {
        if (isPrimitive(context)) {
            return context;
        }
        else if (context instanceof Map) {
            return ((Map) context).get(attr);
        }
        else if (context instanceof Dictionary) {
            return ((Dictionary) context).get(attr);
        }
        //use the reflection to retrieve the value
        String getter = null;
        try {
            Method method;
            //it's supposed to be n existing field in the given {@link bean}
            try {
                Field field = context.getClass().getDeclaredField(attr);
                getter = getGetterName(context.getClass().getDeclaredField(attr));
                method = context.getClass().getMethod(getter);
            } catch (Exception x) {
                try {
                    getter = getGetterName(attr, String.class);
                    method = context.getClass().getMethod(getter);
                } catch (NoSuchMethodException e) {
                    //if method is not "getXXX", try isXXX
                    getter = getGetterName(attr, Boolean.class);
                    method = context.getClass().getMethod(getter);
                } catch (SecurityException e) {
                    throw new IllegalArgumentException("Can not reach method.", e);
                }
            }
            return method.invoke(context);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to reach getter method for variable '" + attr + "' in object of type '" + context.getClass() + "'.", ex);
        }
    }

    private boolean isPrimitive(Object obj) {
        return obj instanceof Number || obj instanceof String ||
                obj instanceof Boolean;
    }

    public static String getGetterName(Field field) {
        return getGetterName(field.getName(), field.getType());
    }

    public static String getGetterName(String fname, Class ftype) {
        String getterName = ftype.isAssignableFrom(Boolean.class) || ftype.isAssignableFrom(boolean.class) ? "is" : "get";
        getterName += fname.substring(0, 1).toUpperCase();
        getterName += fname.substring(1, fname.length());
        return getterName;
    }

    public static String getSetterName(String fieldName) {
        String setterName = "set";
        setterName += fieldName.substring(0, 1).toUpperCase();
        setterName += fieldName.substring(1, fieldName.length());
        return setterName;
    }
}