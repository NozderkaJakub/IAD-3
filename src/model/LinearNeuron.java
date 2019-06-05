package model;

public class LinearNeuron {
	double[] weights;
	double bias;
	double[] inputs;

	public LinearNeuron(int numberOfInputs) {
		this.inputs = new double[numberOfInputs];
		this.weights = new double[numberOfInputs];
		this.bias = 0;
		initWeights();
	}

	void initWeights() {
		if (Math.random() < 0.5)
			this.bias = Math.random() * (-4);
		else
			this.bias = Math.random() * 4;

		for (int i = 0; i < weights.length; i++) {
			if (Math.random() < 0.5)
				this.weights[i] = Math.random() * (-4);
			else
				this.weights[i] = Math.random() * 4;
		}
	}
	
	public void setWeight(int index, double value) {
		this.weights[index] = value;
	}

	public double[] getWeights() {
		return weights;
	}
	
	public double getWeight(int index) {
		return weights[index];
	}

	public void setInputs(double[] inputs) {
		this.inputs = inputs;
	}
	
	public void setBias(double value) {
		this.bias = value;
	}
	
	public double getBias() {
		return this.bias;
	}

	public double getOutput() {
		double output = 0;
		for (int i = 0; i < weights.length; i++) {
			output += this.inputs[i] * this.weights[i];
		}
		output += this.bias;
		return output;
	}

}
