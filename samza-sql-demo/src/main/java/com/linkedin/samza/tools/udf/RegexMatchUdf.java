package com.linkedin.samza.tools.udf;

import java.util.regex.Pattern;
import org.apache.samza.config.Config;
import org.apache.samza.sql.udfs.ScalarUdf;


/**
 * Simple RegexMatch Udf.
 */
public class RegexMatchUdf implements ScalarUdf {
  @Override
  public void init(Config config) {

  }

  @Override
  public Object execute(Object... args) {
    return Pattern.matches((String) args[0], (String) args[1]);
  }
}
