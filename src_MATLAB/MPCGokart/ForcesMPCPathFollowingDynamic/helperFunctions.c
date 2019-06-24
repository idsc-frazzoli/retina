#include "MPCPathFollowing/include/MPCPathFollowing.h"

MPCPathFollowing_float cp(MPCPathFollowing_float v){
	double cp0 = 2.0892;
	double cp1 = -0.0107;
	return cp0+cp1*v;
}

MPCPathFollowing_float cn(MPCPathFollowing_float v){
	double cn0 = -1.5466;
	double cn1 = -0.0293;
	return cn0+cn1*v;
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
