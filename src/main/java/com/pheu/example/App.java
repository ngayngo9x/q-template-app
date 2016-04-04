/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pheu.example;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.pheu.app.AbstractApplication;
import com.pheu.app.AppLauncher;
import com.pheu.app.commands.Command;
import com.pheu.app.core.ShutdownRegistry;
import com.pheu.app.core.ShutdownStage;
import java.util.ArrayList;

/**
 *
 * @author quynt
 */
public class App extends AbstractApplication {
    
    @Inject
    @ShutdownStage
    private Command shutdownCommand;
    
    public static void main(String[] args) {
        AppLauncher.launch(App.class, args);
    }

    @Override
    public void run() {
        System.out.println("Main App run!");
        ((ShutdownRegistry)shutdownCommand).addAction(new AppShutdownImpl());
    }

    @Override
    public Iterable<? extends Module> getModules() {
        ArrayList<Module> modules = new ArrayList<>();
        modules.add(new ApplicationModule());
        return modules;
    }
    
    

}
