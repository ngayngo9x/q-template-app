/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pheu.app;

import com.pheu.app.core.StartupStage;
import com.pheu.app.core.Lifecycle;
import com.pheu.app.commands.ExceptionalCommand;
import com.pheu.app.modules.AppLauncherModule;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.pheu.app.modules.LifecycleModule;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author quynt
 */
public class AppLauncher {

    private static final Logger LOG = Logger.getLogger(AppLauncher.class.getName());

    @Inject
    @StartupStage
    private ExceptionalCommand startupCommand;
    @Inject
    private Lifecycle lifecycle;
    
    private AppLauncher() {
        // This should not be invoked directly.
    }

    private void run(Application application) {
        try {
            configureInjection(application);

            LOG.info("Executing startup actions.");
            // We're an app framework and this is the outer shell - it makes sense to handle all errors
            // before exiting.
            // SUPPRESS CHECKSTYLE:OFF IllegalCatch
            try {
                startupCommand.execute();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Startup action failed, quitting.", e);
                throw Throwables.propagate(e);
            }
            // SUPPRESS CHECKSTYLE:ON IllegalCatch

            try {
                application.run();
            } finally {
                LOG.info("Application run() exited.");
            }
        } finally {
            if (lifecycle != null) {
                lifecycle.shutdown();
            }
            
        }
    }

    private void configureInjection(Application application) {
        Iterable<Module> modules = ImmutableList.<Module>builder()
                .add(new LifecycleModule())
                .add(new AppLauncherModule())
                .addAll(application.getModules())
                .build();

        Injector injector = Guice.createInjector(Modules.combine(modules));
        injector.injectMembers(this);
        injector.injectMembers(application);
    }

    public static void launch(Class<? extends Application> appClass, String... args) {
        Preconditions.checkNotNull(appClass);
        Preconditions.checkNotNull(args);

        try {
            new AppLauncher().run(appClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

}
