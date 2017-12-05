package com.linkedin.samza.tools.udf;

import org.apache.samza.config.Config;
import org.apache.samza.sql.udfs.ScalarUdf;


public class StringContainsUdf implements ScalarUdf {
  @Override
  public void init(Config config) {

  }

  public Object execute(Object... args) {
    return null;
  }
}
