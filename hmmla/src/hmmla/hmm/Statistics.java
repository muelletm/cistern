// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.hmm;

import hmmla.util.Arrays;
import hmmla.util.Numerics;
import hmmla.util.SymbolTable;

import java.io.Serializable;

public class Statistics implements Serializable {

	private static final long serialVersionUID = 1L;
	protected int num_tags;
	protected int num_words;
	private double[][] emissions;
	public double[][] transistions;
	
	public Statistics(int num_tags,int num_words){
		this.num_tags = num_tags;
		this.num_words = num_words;
		
		emissions = new double[num_tags][num_words];
		transistions = new double[num_tags][num_tags];
		setZero();
	}
	
	public Statistics(SymbolTable<String> inputs,SymbolTable<String> outputs){
		this(inputs.size(),outputs.size());
	}

	public Statistics(Statistics statistics) {
		this(statistics.num_tags,statistics.num_words);
		add(statistics);
	}

	public void set(double[][] tr, double[][] em) {
		Arrays.multiArrayCopy(tr, transistions);
		Arrays.multiArrayCopy(em, emissions);
	}

	public double getTransitions(int i, int j) {
		return transistions[i][j];
	}
	
	public double getEmissions(int i, int o) {
		return emissions[i][o];
	}

	public void setZero() {
		
		for (int i=0;i<num_tags;i++){

			for (int o=0;o<num_words;o++){
				emissions[i][o] = 0.0;
			}

			for (int j=0;j<num_tags;j++){
				transistions[i][j] = 0.0;
			}

		}
		
	}

	public void addEmissions(int toIndex, int output, double p) {
		emissions[toIndex][output] += p;
	}

	public void addTransitions(int from, int to, double p) {
		transistions[from][to] += p;
	}

	public void setTransitions(int from, int to, double p) {
		transistions[from][to] = p;
	}

	public void setEmissions(int from, int o, double p) {
		emissions[from][o] = p;
	}

	public void add(Statistics statistics) {
			
		for (int i=0;i<num_tags;i++){

			for (int o=0;o<num_words;o++){
				addEmissions(i, o, statistics.getEmissions(i, o));
			}

			for (int j=0;j<num_tags;j++){
				addTransitions(i, j, statistics.getTransitions(i, j));
			}
			
		}
	}

	public int getNumTags() {
		return num_tags;
	}

	public int getNumOutputs() {
		return num_words;
	}

	public void substract_onehalf(){
		
		for (int i=0;i<num_tags;i++){

			for (int o=0;o<num_words;o++){
				double f;
				try{
					f = Numerics.exp_digamma(getEmissions(i, o));
				}catch (IllegalArgumentException e){
					f = 0.0;
				}
				setEmissions(i, o, f);
			}

			for (int j=0;j<num_tags;j++){
				double f;		
				try{
					f = Numerics.exp_digamma(getEmissions(i, j));
				}catch (IllegalArgumentException e){
					f = 0.0;
				}
				setTransitions(i, j, f);
			}
			
		}
	}
	
	public double totalEmission(){
		
		double total = 0.0;
		
		for (int i=0;i<num_tags;i++){
			for (int o=0;o<num_words;o++){
				total += getEmissions(i, o);
			}
		}
		
		return total;
	}
	
	public double totalTransmission(){
		
		double total = 0.0;
		
		for (int i=0;i<num_tags;i++){

			for (int j=0;j<num_tags;j++){
				total += getTransitions(i, j);
			}
		}
		
		return total;
	}

}