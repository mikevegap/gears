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

package org.openlogics.gears.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This interface allows to receive transformed beans as result from a query
 * @author Miguel Vega
 * @version $Id: ObjectResultSetHandler.java 0, 2012-10-05 01:15 mvega $
 */
public interface ObjectResultSetHandler<S> {
    /**
     * This method will return the object created from query results.
     * @param obj an object as result from query
     */
    public void handle(S obj)throws SQLException;
}
