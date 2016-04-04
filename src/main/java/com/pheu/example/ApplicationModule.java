/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pheu.example;

import com.google.inject.AbstractModule;
import static com.pheu.app.modules.LifecycleModule.bindStartupAction;

/**
 *
 * @author quynt
 */
public class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        bindStartupAction(binder(), ApplicationCommandImpl.class);
    }

}
