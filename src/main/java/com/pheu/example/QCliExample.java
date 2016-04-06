package com.pheu.example;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class QCliExample {

	@Parameter(names = {"-help", "--help"}, description = "Help", help = true)	
	private boolean isHelp = false;
	
	@Parameter(names = {"-h", "-host"}, description = "Host of applicaton")
	private String host;
	
	@Parameter(names = {"-p", "-port"}, description = "Port of applicaton")
	private int port;
	
	public static void main(String[] args) {
		
		QCliExample main = new QCliExample();
		JCommander cmd = new JCommander(main, args);
		if (main.isHelp) {
			cmd.usage();
			return;
		}
		main.run();
	}
	
	public void run () {
		System.out.printf("%s:%d ", host, port);
	}
	
}
