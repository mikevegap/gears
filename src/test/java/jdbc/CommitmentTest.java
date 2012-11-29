/*
 * gears
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

package jdbc;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.openlogics.gears.jdbc.BatchQuery;
import org.openlogics.gears.jdbc.DataStore;
import org.openlogics.gears.jdbc.JdbcDataStore;
import org.openlogics.gears.jdbc.Query;

import java.sql.SQLException;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;

/**
 * @author Miguel Vega
 * @version $Id: CommitmentTest.java 0, 2012-11-23 7:21 PM mvega $
 */
public class CommitmentTest extends TestStub {

    @Test
    public void testLargeInsertion() {

        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "postgres");
        props.setProperty("url", "jdbc:postgresql://localhost:5432/foo");
        props.setProperty("driver", "org.postgresql.Driver");
        //props.setProperty("ssl","true");


        //DataStore ds = new JdbcDataStore(props);
        DataStore ds = new JdbcDataStore(basicDataSource);
        ds.setAutoCommit(false);

        long curr = 0;

        try {
            viewAll(ds);
            curr = countAll(ds);
            //assertEquals(5, countAll(ds));
            /*
            BatchQuery q = new BatchQuery("insert into FOO (FOO_FNAME, FOO_LNAME) values (#{a}, #{b})", ds).
                    addBatch(ImmutableMap.of("a", "miguel", "b", "vega")).
                    addBatch(ImmutableMap.of("a", "miguel", "b", "vega")).
                    addBatch(ImmutableMap.of("a", "miguel", "b", "vega")).
                    addBatch(ImmutableMap.of("a", "miguel", "b", "vega"));

            ds.update(q);
            */

            assertEquals(curr, countAll(ds));

            ds.update(new Query("insert into FOO (FOO_FNAME, FOO_LNAME) values (#{a}, #{b})", ImmutableMap.of("a", "miguel", "b", "vega")));
            //assertEquals(curr, countAll(ds));

            //ds.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                //ds.rollBack();
                //assertEquals(curr + 4, countAll(ds));
                //
                viewAll(ds);
                ds.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testTruncatedInsertion() {
        DataStore ds = new JdbcDataStore(basicDataSource);
        ds.setAutoCommit(false);

        try {
            assertEquals(5, countAll(ds));

            BatchQuery q = new BatchQuery("insert into FOO (FOO_FNAME, FOO_LNAME) values (#{a}, #{b})", ds).
                    addBatch(ImmutableMap.of("a", "miguel", "b", "vega")).
                    addBatch(ImmutableMap.of("a", "miguel", "b", "vega")).
                    addBatch(ImmutableMap.of("a", "miguel", "b", "vega")).
                    addBatch(ImmutableMap.of("a-mmmm", "miguel", "b-can not be null", "vega"));

            ds.update(q);

            ds.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ds.rollBack();
                assertEquals(5, countAll(ds));
                //
                viewAll(ds);
                ds.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}