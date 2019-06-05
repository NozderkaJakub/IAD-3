package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class RadialNeuron {
	double input;
	double pattern;
	double sigma;
	public int number;

	public RadialNeuron() {
		this.pattern = 0;
		this.sigma = 0;
		this.number = 0;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setInput(double input) {
		this.input = input;
	}
	
	private double getInput() {
		return this.input;
	}

	public void setPattern(double pattern) {
		this.pattern = pattern;
	}
	
	public double getPattern() {
		return this.pattern;
	}

	public void setSigma(double sigma) {
		this.sigma = sigma;
	}

	public double getOutput() {		
		return Math.exp(-(Math.pow(checkDistance(input), 2) / 2 * Math.pow(this.sigma, 2)));
	}

	double distance(double x) {
		return Math.sqrt(Math.pow((x - this.pattern), 2));
	}

	public void saveOutput(double weight) {
		File file = new File("output" + this.number + ".txt");
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(file, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.println(getInput() + ";" + getOutput() * weight);
		out.close();
	}

	public double checkDistance(double point) {
		return Math.abs(this.pattern - point);
	}

}
