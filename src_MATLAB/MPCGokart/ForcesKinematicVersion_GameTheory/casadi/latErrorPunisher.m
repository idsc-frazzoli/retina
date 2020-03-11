function [cost] = latErrorPunisher(v)
%UNTITLED2 Summary of this function goes here
%   Detailed explanation goes here
x=min(v,0);
cost=x^2;
end

