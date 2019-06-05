package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import model.*;

public class Program {
	private LinearNeuron linear;
	private List<RadialNeuron> radial;
	private double[][] points;
	private double[][] pointsToApproximate;
	private double alpha;
	private int shuffle;

	public Program(int noOfPatterns) throws IOException {
		linear = new LinearNeuron(noOfPatterns);
		radial = new ArrayList<RadialNeuron>();
		for (int i = 0; i < noOfPatterns; i++) {
			radial.add(i, new RadialNeuron(i + 1));
		}

		points = new double[countLines("train.txt")][2];
		pointsToApproximate = new double[countLines("test.txt")][2];
		getData("train.txt", points);
		sortPoints(points);
		chooseRandomRadials();
		saveRadials();
		setFiles();
		double sigma = sigma(noOfPatterns);
		for (int i = 0; i < radial.size(); i++) {
			radial.get(i).setSigma(sigma);
		}
		alpha = 0.01;
		shuffle = ThreadLocalRandom.current().nextInt(0, points.length);
	}

	public void train() {
		double error = 0;
		int iterator = 0;
		do {
			Vector<Double> derivativeWeights = new Vector<Double>();
			for (int i = 0; i < radial.size(); i++) {
				derivativeWeights.add(0.0);
			}

			List<Integer> occurs = new ArrayList<Integer>();
			error = 0;
			double[] linearInputs = new double[radial.size()];
			double derivativeBias = 0;

			for (int i = 0; i < points.length; i++) {
				Vector<Double> radialOutputs = new Vector<Double>();

				while (checkIfOccurs(occurs)) {
					shuffle = ThreadLocalRandom.current().nextInt(0, points.length);
				}
				occurs.add(shuffle);

				for (int j = 0; j < radial.size(); j++) {
					radial.get(j).setInput(points[shuffle][0]);
					linearInputs[j] = radial.get(j).getOutput();
					radialOutputs.add(radial.get(j).getOutput());
				}
				linear.setInputs(linearInputs);

				error += Math.pow((linear.getOutput() - points[shuffle][1]), 2);
				derivativeBias += (linear.getOutput() - points[shuffle][1]);

				for (int j = 0; j < radialOutputs.size(); j++) {
					derivativeWeights.set(j, derivativeWeights.get(j)
							+ ((linear.getOutput() - points[shuffle][1]) * radialOutputs.get(j)));
				}
			}

			error /= (2 * points.length);
			System.out.println(error);
			derivativeBias /= points.length;
			for (int i = 0; i < derivativeWeights.size(); i++) {
				derivativeWeights.set(i, (derivativeWeights.get(i) / points.length));
			}

			linear.setBias(linear.getBias() - alpha * derivativeBias);
			for (int i = 0; i < radial.size(); i++) {
				linear.setWeight(i, (linear.getWeight(i) - alpha * derivativeWeights.get(i)));
			}
			iterator++;
//		} while (error > 0.07);
		} while (iterator < 20000 || error > 0.09);
		
		System.out.println("Koniec nauki");

	}

	public void approximate() throws IOException {
		getData("test.txt", pointsToApproximate);
		sortPoints(pointsToApproximate);
		double[] linearInputs = new double[radial.size()];

		for (int i = 0; i < pointsToApproximate.length; i++) {

			for (int j = 0; j < radial.size(); j++) {
				radial.get(j).setInput(pointsToApproximate[i][0]);
				radial.get(j).saveOutput(linear.getWeights()[j]);
				linearInputs[j] = radial.get(j).getOutput();
			}
			linear.setInputs(linearInputs);
			saveOutput(pointsToApproximate[i][0], linear.getOutput());
			
		}
	}

	private void sortPoints(double[][] points) {
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < points.length - 1; j++) {
				double[] buf = new double[2];
				if (points[j][0] > points[j + 1][0]) {
					buf = points[j];
					points[j] = points[j + 1];
					points[j + 1] = buf;
				}
			}
		}
	}

	private void setFiles() throws IOException {
		File linearOutputFile = new File("output0.txt");

		if (linearOutputFile.exists())
			linearOutputFile.delete();
		linearOutputFile.createNewFile();

		for (int i = 1; i <= radial.size(); i++) {
			File radialOutputFile = new File("output" + i + ".txt");
			if (radialOutputFile.exists())
				radialOutputFile.delete();
			radialOutputFile.createNewFile();
		}
	}

	private void saveOutput() {
		File file = new File("output0.txt");
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(file));
		} catch (IOException e) {
			e.getStackTrace();
		}

		for (int i = 0; i < points.length; i++) {
			double[] linearInputs = new double[radial.size()];
			for (int j = 0; j < radial.size(); j++) {
				radial.get(j).setInput(points[i][0]);
				linearInputs[j] = radial.get(j).getOutput();
			}
			linear.setInputs(linearInputs);
			out.println(points[i][0] + ";" + linear.getOutput());
		}

		out.close();
	}

	private void getData(String filename, double[][] points) throws IOException {
		InputStream is = new FileInputStream("train.txt");
		BufferedReader buf = new BufferedReader(new InputStreamReader(is));
		String line = buf.readLine();
		int i = 0;
		while (line != null) {
			points[i][0] = Double.parseDouble(line.split(" ")[0]);
			points[i][1] = Double.parseDouble(line.split(" ")[1]);
			line = buf.readLine();
			i++;
		}
		buf.close();
	}

	private double sigma(int noOfPatterns) {
//		System.out.println(maxDistance());
		return maxDistance() / Math.sqrt(2 * noOfPatterns);
	}

	private double maxDistance() {
		double distance = 0;
		for (int i = 0; i < radial.size() - 1; i++) {
			for (int j = 0; j < radial.size() - 1; j++) {
				double dist = Math.abs(radial.get(i).getPattern() - radial.get(i + 1).getPattern());
				if (dist > distance)
					distance = dist;
			}
		}
		return distance;
	}

	private void chooseRandomRadials() {
		for (int i = 0; i < radial.size(); i++) {
			radial.get(i).setPattern(points[ThreadLocalRandom.current().nextInt(0, points.length)][0]);
		}
	}

	private boolean checkIfOccurs(List<Integer> occur) {
		for (int i = 0; i < occur.size(); i++) {
			if (shuffle == occur.get(i)) {
				return true;
			}
		}
		return false;
	}

	private int countLines(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		int lines = 0;
		while (reader.readLine() != null)
			lines++;
		reader.close();
		return lines;
	}

	private void saveRadials() {
		File file = new File("radials.txt");
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < radial.size(); i++) {
			out.println(radial.get(i).getPattern());
		}
		out.close();
	}

	private void saveOutput(double x, double value) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter("output0.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}

		out.println(x + ";" + value);
		out.close();
	}

}
