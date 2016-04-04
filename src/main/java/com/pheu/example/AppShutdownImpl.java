/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pheu.example;

import com.pheu.app.commands.Command;

/**
 *
 * @author quynt
 */
public class AppShutdownImpl implements Command {

    @Override
    public void execute() throws RuntimeException {
        System.out.println("AppShutdownImpl.execute");
    }
    
}
