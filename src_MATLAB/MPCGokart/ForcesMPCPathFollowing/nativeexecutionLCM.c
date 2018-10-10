/* A simple server in the internet domain using TCP
   The port number is passed as an argument */
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

#include "MPCPathFollowing/include/MPCPathFollowing.h"
#include <lcm/lcm.h>

/**
 * TCP Uses 2 types of sockets, the connection socket and the listen socket.
 * The Goal is to separate the connection phase from the data exchange phase.
 * */

int listen_sock;
int sock;
FILE *file;
FILE *solverFile;
bool finished = false;
bool running = true;

/* declare FORCES variables and structures */
int i, exitflag;
MPCPathFollowing_params myparams;
MPCPathFollowing_output myoutput;
MPCPathFollowing_info myinfo;
MPCPathFollowing_float minusA_times_x0[2];

extern void MPCPathFollowing_casadi2forces(double *x, double *y, double *l, double *p,
                                                double *f, double *nabla_f, double *c, double *nabla_c,
                                                double *h, double *nabla_h, double *H, int stage);


MPCPathFollowing_extfunc pt2Function =&MPCPathFollowing_casadi2forces;


int main(int argc, char *argv[]) {
	lcm_t * lcm = lcm_create(NULL)
	if(!lcm)
		return 1;
	
	exitflag = MPCPathFollowing_solve(&myparams, &myoutput, &myinfo, solverFile, pt2Function);
	lcm_destroy(lcm);
	return 0;
}
