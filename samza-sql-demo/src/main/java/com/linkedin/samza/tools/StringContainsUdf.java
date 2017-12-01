package com.linkedin.samza.tools;

import org.apache.samza.config.Config;
import org.apache.samza.sql.udfs.ScalarUdf;


public class StringContainsUdf implements ScalarUdf {
  @Override
  public void init(Config config) {

  }

  @Override
  public Object execute(Object... args) {
    return null;
  }
}
