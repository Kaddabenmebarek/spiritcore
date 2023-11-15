package com.idorsia.research.spirit.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

public class StatUtils {

	
	/**
	 * Calculate Kruskal-Walis (http://en.wikipedia.org/wiki/Kruskal%E2%80%93Wallis_one-way_analysis_of_variance)
	 * @param values
	 * @return
	 */
	public static double getKruskalWallis(List<double[]> values) {
		
		
		//Assign ranks
		assert values.size()>1;
		
		List<Double> allDoubles = new ArrayList<>();
		for (double[] a : values) {
			assert a.length>0;
			for (double d : a) {
				allDoubles.add(d);
			}
		}
		int N = allDoubles.size();
		Collections.sort(allDoubles);
		double[] allDoublesArray = new double[allDoubles.size()];
		for (int i = 0; i < allDoubles.size(); i++) allDoublesArray[i] = allDoubles.get(i);
		List<double[]> ranks = new ArrayList<double[]>();
		for (double[] a : values) {
			double[] rankArray = new double[a.length];
			ranks.add(rankArray);
			for (int i = 0; i < a.length; i++) {
				int r = Arrays.binarySearch(allDoublesArray, a[i]);
				assert r>=0;
				int r1 = r, r2 = r;
				while(r1>0 && allDoublesArray[r1-1]==a[i]) r1--;
				while(r2<allDoublesArray.length-1 && allDoublesArray[r2+1]==a[i]) r2++;
				rankArray[i] = (r1+r2)/2.0 + 1; 
			}
		}
		
		//Calculate rank average per actionGroup
		List<Double> rankSum = new ArrayList<Double>();
		for (double[] a : ranks) {
			double sum = 0;
			for (double d: a) sum+=d;
			rankSum.add(sum);
		}
		
		
		
		double sum = 0;
		for (int i = 0; i < ranks.size(); i++) {
			sum += rankSum.get(i) * rankSum.get(i) / ranks.get(i).length;
		}
		double H = 12.0/(N*(N+1)) * sum - 3 * (N+1);
		
		ChiSquaredDistribution chi = new ChiSquaredDistribution(values.size()-1);
		double K = 1-chi.cumulativeProbability(H);

		return K;
	}	
	
	public static Double getMedian(List<Double> doubles) {
		Collections.sort(doubles);
		if(doubles.size()==0) {
			return null;
		} else if(doubles.size()%2==0) {
			return (doubles.get(doubles.size()/2-1) + doubles.get(doubles.size()/2))/2;
		} else {
			return doubles.get(doubles.size()/2);
		}		
	}
	
	public static Double getMean(List<Double> doubles) {
		if(doubles.size()==0) {
			return null;
		} else {
			int i = 0;
			double sum = 0;
			for (Double d : doubles) {
				if(d==null) continue;
				sum += d;
				i++;
			}
			return i==0? null: sum/i;
		}		
	}
	public static double[] getFences(List<Double> doubles) {
		if(doubles.size()==0) return null;
		if(doubles.size()<=4) return null;
		
		//double mean = getMean(doubles);
		Collections.sort(doubles);
		double indexQ1 = (doubles.size())*.25;
		double indexQ3 = (doubles.size())*.75;
		
		
		double q1 =  doubles.get((int)indexQ1) + (indexQ1-(int)indexQ1) * (doubles.get((int)indexQ1+1)-doubles.get((int)indexQ1));
		double q3 =  doubles.get((int)indexQ3) + (indexQ3-(int)indexQ3) * (doubles.get((int)indexQ3+1)-doubles.get((int)indexQ3));
		double interquartile = q3-q1;
		double l3 = q1 - 3 * interquartile; 
		double l2 = q1 - 2 * interquartile;
		double l1 = q1 - 1 * interquartile;
		double h1 = q3 + 1 * interquartile;
		double h2 = q3 + 2 * interquartile;
		double h3 = q3 + 3 * interquartile; 
		
		return new double[] {l3, l2, l1, h1, h2, h3};
	}
	
	
	public static double getStandardDeviationOfMean(List<Double> doubles) {
		return getStandardDeviation(doubles, getMean(doubles));
	}
	public static Double getStandardDeviation(List<Double> doubles, Double mean) {
		double sum = 0;
		int i = 0;
		for (Double d : doubles) {
			if(d==null) continue;
			sum += (d-mean)*(d-mean);
			i++;
		}
		return i==0? null: Math.sqrt(sum/i);
	}
	
	public static void main(String[] args) {
		int count = 0;
		int N = 1000;
		for(int t=0; t<N; t++) {
			List<double[]> doubles = new ArrayList<double[]>();
			for (int i = 0; i < 3; i++) {
				double[] array = new double[10];
				doubles.add(array);
				for (int j = 0; j < array.length; j++) {
					array[j] = Math.random()*10;
				} 			
			} 
			double r = getKruskalWallis(doubles);
			if(r<0.05) count++;
			System.out.println("["+t+"] KW="+r);
		}
		System.out.println("==> percentage of false positive "+(1.0*count/N)+" ~ 0.05");

	}

}
