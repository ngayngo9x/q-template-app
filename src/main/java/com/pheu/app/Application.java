/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pheu.app;

import com.google.inject.Module;

/**
 *
 * @author quynt
 */
/**
 * An application that supports a limited lifecycle and optional binding of guice modules.
 */
public interface Application extends Runnable {

  /**
   * Returns binding modules for the application.
   *
   * @return Application binding modules.
   */
  Iterable<? extends Module> getModules();
}
