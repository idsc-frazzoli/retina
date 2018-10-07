clear all
powersteer = csvread('steercalibration.csv');
powersteer = [powersteer,powersteer(:,3)-117.2];
powersteerSorted = sortrows(powersteer,1);
input.data = powersteerSorted;
s=latexTable(input);