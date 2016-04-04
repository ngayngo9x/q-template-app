/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pheu.app.commands;

/**
 *
 * @author quynt
 */
public class ExceptionCommandImpl implements ExceptionalCommand {

    @Override
    public void execute() throws Exception {
        System.out.println("ExceptionCommandImpl.execute");
    }
    
}
