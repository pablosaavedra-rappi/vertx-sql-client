/*
 * Copyright (c) 2011-2020 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package io.vertx.sqlclient.tck;

import io.vertx.ext.unit.TestContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.data.Numeric;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;

public abstract class BinaryDataTypeEncodeTestBase extends DataTypeTestBase {
  protected abstract String statement(String... parts);

  @Test
  public void testSmallInt(TestContext ctx) {
    testEncodeGeneric(ctx, "test_int_2", Short.class, (short) Short.MIN_VALUE);
  }
  
  @Test
  public void testInteger(TestContext ctx) {
    testEncodeGeneric(ctx, "test_int_4", Integer.class, (int) Integer.MIN_VALUE);
  }
  
  @Test
  public void testBigInt(TestContext ctx) {
    testEncodeGeneric(ctx, "test_int_8", Long.class, (long) Long.MIN_VALUE);
  }

  @Test
  public void testFloat4(TestContext ctx) {
    testEncodeGeneric(ctx, "test_float_4", Float.class, (float) -3.402823e38F);
  }

  @Test
  public void testDouble(TestContext ctx) {
    testEncodeGeneric(ctx, "test_float_8", Double.class, (double) Double.MIN_VALUE);
  }

  @Test
  public void testNumeric(TestContext ctx) {
    testEncodeGeneric(ctx, "test_numeric", Numeric.class, Numeric.parse("-999.99"));
  }

  @Test
  public void testDecimal(TestContext ctx) {
    testEncodeGeneric(ctx, "test_decimal", Numeric.class, Numeric.parse("-12345"));
  }

  @Test
  public void testChar(TestContext ctx) {
    testEncodeGeneric(ctx, "test_char", String.class, "newchar0");
  }

  @Test
  public void testVarchar(TestContext ctx) {
    testEncodeGeneric(ctx, "test_varchar", String.class, "newvarchar");
  }

  @Test
  public void testBoolean(TestContext ctx) {
    testEncodeGeneric(ctx, "test_boolean", Boolean.class, false);
  }

  @Test
  public void testDate(TestContext ctx) {
    testEncodeGeneric(ctx, "test_date", LocalDate.class, LocalDate.parse("1999-12-31"));
  }

  @Test
  public void testTime(TestContext ctx) {
    testEncodeGeneric(ctx, "test_time", LocalTime.class, LocalTime.of(12,1,30));
  }
  
  @Test
  public void testNullValues(TestContext ctx) {
	    connector.connect(ctx.asyncAssertSuccess(conn -> {
	        conn
	          .preparedQuery(statement("UPDATE basicdatatype SET" + 
	        		  " test_int_2 = ", 
	        	      ", test_int_4 = ",
	        	      ", test_int_8 = ",
	        	      ", test_float_4 = ",
	        	      ", test_float_8 = ",
	        	      ", test_numeric = ",
	        	      ", test_decimal = ",
	        	      ", test_boolean = ",
	        	      ", test_char = ",
	        	      ", test_varchar = ",
	        	      ", test_date = ",
	        	      ", test_time = ",
	        		  " WHERE id = 2"))
	          .execute(Tuple.tuple()
	        		  .addValue(null)
	        		  .addValue(null)
	        		  .addValue(null)
	        		  .addValue(null)
	        		  .addValue(null)
	        		  .addValue(null)
	        		  .addValue(null)
	        		  .addValue(null)
	        		  .addValue(null)
	        		  .addValue(null)
	        		  .addValue(null)
	        		  .addValue(null), 
	        		  ctx.asyncAssertSuccess(updateResult -> {
	          conn
	            .preparedQuery("SELECT * FROM basicdatatype WHERE id = 2")
	            .execute(ctx.asyncAssertSuccess(result -> {
	            ctx.assertEquals(1, result.size());
	            Row row = result.iterator().next();
	            ctx.assertEquals(13, row.size());
	            ctx.assertEquals(2, row.getInteger(0));
	            for (int i = 1; i < 13; i++) {
	              ctx.assertNull(row.getValue(i));
	            }
	            conn.close();
	          }));
	        }));
	      }));
  }
  
  protected <T> void testEncodeGeneric(TestContext ctx,
                                       String columnName,
                                       Class<T> clazz,
                                       T expected) {
    connector.connect(ctx.asyncAssertSuccess(conn -> {
      conn
        .preparedQuery(statement("UPDATE basicdatatype SET " + columnName + " = ", " WHERE id = 2"))
        .execute(Tuple.tuple().addValue(expected), ctx.asyncAssertSuccess(updateResult -> {
        conn
          .preparedQuery("SELECT " + columnName + " FROM basicdatatype WHERE id = 2")
          .execute(ctx.asyncAssertSuccess(result -> {
          ctx.assertEquals(1, result.size());
          Row row = result.iterator().next();
          ctx.assertEquals(1, row.size());
          ctx.assertEquals(expected, row.getValue(0));
          ctx.assertEquals(expected, row.getValue(columnName));
//        ctx.assertEquals(expected, row.get(clazz, 0));
//        ColumnChecker.checkColumn(0, columnName)
//          .returns(Tuple::getValue, Row::getValue, expected)
//          .returns(byIndexGetter, byNameGetter, expected)
//          .forRow(row);
          conn.close();
        }));
      }));
    }));
  }
}
