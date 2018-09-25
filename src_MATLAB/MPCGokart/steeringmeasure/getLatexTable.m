clear all
powersteer = csvread('steercalibration.csv');
powersteerSorted = sortrows(powersteer,1);
input.data = powersteerSorted;
s=latexTable(input);