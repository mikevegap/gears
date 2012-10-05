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

package org.openlogics.gears.text;

/**
 * @author Miguel Vega
 * @version $Id: ELEvaluator.java 0, 2012-09-29 11:16 mvega $
 */
public interface ELEvaluator<U, V> {
    /**
     * Evaluates the given text and process parameters with given context.
     * @param textToEvaluate
     * @param context
     * @return
     */
    public U evaluate(String textToEvaluate, V context);
}
