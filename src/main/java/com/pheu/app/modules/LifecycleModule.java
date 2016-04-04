// =================================================================================================
// Copyright 2011 Twitter, Inc.
// -------------------------------------------------------------------------------------------------
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this work except in compliance with the License.
// You may obtain a copy of the License in the LICENSE file, or at:
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// =================================================================================================

package com.pheu.app.modules;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Singleton;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.inject.multibindings.Multibinder;
import com.pheu.app.commands.Command;
import com.pheu.app.commands.ExceptionalCommand;
import com.pheu.app.core.Lifecycle;
import com.pheu.app.core.ShutdownRegistry;
import com.pheu.app.core.ShutdownRegistry.ShutdownRegistryImpl;
import com.pheu.app.core.ShutdownStage;
import com.pheu.app.core.StartupRegistry;
import com.pheu.app.core.StartupStage;

/**
 * Binding module for startup and shutdown controller and registries.
 *
 * Bindings provided by this module:
 * <ul>
 *   <li>{@code @StartupStage ExceptionalCommand} - Command to execute all startup actions.
 *   <li>{@code ShutdownRegistry} - Registry for adding shutdown actions.
 *   <li>{@code @ShutdownStage Command} - Command to execute all shutdown commands.
 * </ul>
 *
 * If you would like to register a startup action that starts a local network service, please
 * consider using {@link LocalServiceRegistry}.
 *
 * @author William Farner
 */
public class LifecycleModule extends AbstractModule {

  /**
   * Binding annotation used for local services.
   * This is used to ensure the LocalService bindings are visibile within the package only, to
   * prevent injection inadvertently triggering a service launch.
   */
  @BindingAnnotation
  @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
  @interface Service { }

  @Override
  protected void configure() {
    bind(Lifecycle.class).in(Singleton.class);

    bind(Key.get(ExceptionalCommand.class, StartupStage.class)).to(StartupRegistry.class);
    bind(StartupRegistry.class).in(Singleton.class);

    bind(ShutdownRegistry.class).to(ShutdownRegistryImpl.class);
    bind(Key.get(Command.class, ShutdownStage.class)).to(ShutdownRegistryImpl.class);
    bind(ShutdownRegistryImpl.class).in(Singleton.class);
    
    bindStartupAction(binder(), ShutdownHookRegistration.class);
  }

  /**
   * Thrown when a local service fails to launch.
   */
  public static class LaunchException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4785582537642051857L;

	public LaunchException(String msg) {
      super(msg);
    }

    public LaunchException(String msg, Throwable cause) {
      super(msg, cause);
    }
  }

  /**
   * Adds a startup action to the startup registry binding.
   *
   * @param binder Binder to bind against.
   * @param actionClass Class to bind (and instantiate via guice) for execution at startup.
   */
  public static void bindStartupAction(Binder binder,
      Class<? extends ExceptionalCommand> actionClass) {

    Multibinder.newSetBinder(binder, ExceptionalCommand.class, StartupStage.class)
        .addBinding().to(actionClass);
  }

  /**
   * Startup command to register the shutdown registry as a process shutdown hook.
   */
  private static class ShutdownHookRegistration implements Command {
    private final Command shutdownCommand;

    @Inject ShutdownHookRegistration(@ShutdownStage Command shutdownCommand) {
      this.shutdownCommand = checkNotNull(shutdownCommand);
    }

    @Override public void execute() {
      Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        @Override public void run() {
          shutdownCommand.execute();
        }
      }, "ShutdownRegistry-Hook"));
    }
  }

}
