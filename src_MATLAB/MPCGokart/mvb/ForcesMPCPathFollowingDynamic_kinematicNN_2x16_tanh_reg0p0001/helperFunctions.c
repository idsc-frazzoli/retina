#include "MPCPathFollowing/include/MPCPathFollowing.h"

MPCPathFollowing_float cp(MPCPathFollowing_float v){
	double cp0 = 1.9173276271;
	double cp1 = -0.0113682655;
	double cp2 = -0.0150793283;
	double cp3 = 0.0023869979;
	return cp0+cp1*v+cp2*v*v+cp3*v*v*v;
}

MPCPathFollowing_float cn(MPCPathFollowing_float v){
	double cn0 = -1.4265329731;
	double cn1 = -0.1612157772;
	double cn2 = 0.0503284643;
	double cn3 = -0.0048860339;
	return cn0+cn1*v+cn2*v*v+cn3*v*v*v;
}

MPCPathFollowing_float getMaxAcc(MPCPathFollowing_float v){
	double si = 0.5+1.5*v-2*v*v*v;
	double st = 0.5;
	if(v>st)
	    return cp(v);
	else if(v>-st)
	{
	    double posval = cp(st);
	    double negval = -cn(st);
	    return negval*(1-si)+posval*si;
	}
	else
	    return -cn(-v);
}

MPCPathFollowing_float min(MPCPathFollowing_float a,MPCPathFollowing_float b){
	if(a<b)
		return a;
	else
		return b;
}

MPCPathFollowing_float getInitAB(MPCPathFollowing_float ab, MPCPathFollowing_float dotab, MPCPathFollowing_float v, double time){
	return min(getMaxAcc(v)-0.01,ab+dotab*time);
	//return ab+dotab*time;
}

MPCPathFollowing_float getInitSteer(MPCPathFollowing_float beta, MPCPathFollowing_float dotbeta, double time){
	MPCPathFollowing_float nextvalue=beta+dotbeta*time;
	if(nextvalue<-0.5)
		nextvalue = -0.5;
	if(nextvalue>0.5)
		nextvalue = 0.5;
	return nextvalue;
}
