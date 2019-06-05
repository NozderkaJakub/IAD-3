package view;

import java.io.IOException;

import controller.*;

public class Main {
	public static void main(String[] args) throws IOException {
		Program program = new Program(41);
		
		program.algorithm();
	}
}
