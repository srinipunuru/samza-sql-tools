package com.linkedin.samza.tools;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.lang3.StringUtils;


/**
 * Simple command line helper util.
 */
public class CommandLineHelper {
  public static Option createOption(String shortOpt, String longOpt, String argName, boolean required,
      String description) {
    OptionBuilder optionBuilder = OptionBuilder.withLongOpt(longOpt).withDescription(description).isRequired(required);

    if (!StringUtils.isEmpty(argName)) {

      optionBuilder = optionBuilder.withArgName(argName).hasArg();
    }

    return optionBuilder.create(shortOpt);
  }
}
