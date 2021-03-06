/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2011 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.core.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Configure Logback
 *
 * @since 2.12
 */
public final class Logback {

  private Logback() {
    // only static methods
  }

  public static void configure(String classloaderPath) {
    InputStream input = Logback.class.getResourceAsStream(classloaderPath);
    if (input == null) {
      throw new IllegalArgumentException("Logback configuration not found in classloader: " + classloaderPath);
    }
    configure(input);
  }

  public static void configure(File logbackFile) {
    try {
      FileInputStream input = FileUtils.openInputStream(logbackFile);
      configure(input);
    } catch (IOException e) {
      throw new IllegalArgumentException("Fail to load the Logback configuration: " + logbackFile, e);
    }
  }

  /**
   * Note that this method closes the input stream
   */
  private static void configure(InputStream input) {
    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    try {
      JoranConfigurator configurator = new JoranConfigurator();
      configurator.setContext(lc);
      lc.reset();
      configurator.doConfigure(input);
    } catch (JoranException e) {
      // StatusPrinter will handle this
    } finally {
      IOUtils.closeQuietly(input);
    }
    StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
  }
}
