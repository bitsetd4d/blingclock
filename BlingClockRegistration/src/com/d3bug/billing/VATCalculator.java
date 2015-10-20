package com.d3bug.billing;



public class VATCalculator {
	
	private long grossPence;
	private long netPence;
	private long vatPence;
	private double vatRate;
	private long check1;
	private long check2;
	
	public VATCalculator(long grossPence,double vatRate) {
		this.grossPence = grossPence;
		this.vatRate = vatRate;
		calculate();
	}

	public double getVATRate() { return vatRate; }
	public long getVATAmountPence() { return vatPence; }
	public long getNetAmountPence() { return netPence; }
	public long getGrossAmountPence() { return grossPence; }

	private void calculate() {
		vatPence = (int)(0.5 + (grossPence * vatRate / (1 + vatRate)));
		netPence = (int)(grossPence - vatPence);
		check1 = (int)(netPence * vatRate) - vatPence;
		check2 = (int)(grossPence - (netPence + vatPence));
	}

	@Override
	public String toString() {
		return "VATCalculator [grossPence=" + grossPence + ", netPence="
				+ netPence + ", vatPence=" + vatPence + ", vatRate=" + vatRate
				+ ",check1="+check1+", check2="+check2+"]";
	}

	public static void main(String[] args) {
		for (int i=100; i<100000; i++) {
			VATCalculator c = new VATCalculator(i,0.2);
			if (Math.abs(c.check1) > 1 || c.check2 != 0) {
				System.out.println(new VATCalculator(i,0.2));
			}
		}
	}
	
}
