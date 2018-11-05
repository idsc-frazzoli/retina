/* A simple server in the internet domain using TCP
   The port number is passed as an argument */
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <byteswap.h>

#include "MPCPathFollowing/include/MPCPathFollowing.h"
#include <lcm/lcm.h>
#include "idsc_BinaryBlob.c"
#include "definitions.c"

#define N 31
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
lcm_t * lcm;

#define DATASIZE 100
struct ControlAndStateMsg cns [DATASIZE];

/* declare FORCES variables and structures */
int i, exitflag;
MPCPathFollowing_params myparams;
MPCPathFollowing_output myoutput;
MPCPathFollowing_info myinfo;
MPCPathFollowing_float minusA_times_x0[2];

struct StateMsg lastStateMsg;
struct PathMsg lastPathMsg;
struct ParaMsg lastParaMsg;

extern void MPCPathFollowing_casadi2forces(double *x, double *y, double *l, double *p,
                                                double *f, double *nabla_f, double *c, double *nabla_c,
                                                double *h, double *nabla_h, double *H, int stage);


MPCPathFollowing_extfunc pt2Function =&MPCPathFollowing_casadi2forces;



void sendEmptyControlAndStates(lcm_t * lcm){
	struct _idsc_BinaryBlob blob;
	for (int i = 0; i<N; i++){
		cns[i].control.uL = 1;
		cns[i].control.uR = 2;
		cns[i].control.udotS = 3;
		cns[i].control.uB = 4;
		cns[i].state.Ux = 5;
		cns[i].state.Uy = 6;
		cns[i].state.dotPsi = 7;
		cns[i].state.X = 8;
		cns[i].state.Y = 9;
		cns[i].state.Psi = 10;
		cns[i].state.w2L = 11;
		cns[i].state.w2R = 12;
		cns[i].state.s = 13;
	}
	
	blob.data_length = sizeof(struct ControlAndStateMsg)*N;
	blob.data = (int8_t*)&cns;

	for (int i = 0; i< 1; i++){
		if(idsc_BinaryBlob_publish(lcm, "mpc.forces.cns", &blob)==0)
			printf("published test message%d\n",sizeof(struct ControlAndStateMsg)*N);
		else
			printf("error while publishing message\n");
	}
}

static void para_handler(const lcm_recv_buf_t *rbuf,
        const char *channel, const idsc_BinaryBlob *msg, void *userdata){
	printf("received path message\n");
	memcpy((int8_t*)&lastParaMsg, msg->data, msg->data_length);
	printf("max speed: %f\n",lastParaMsg.para.speedLimit);
}

static void path_handler(const lcm_recv_buf_t *rbuf,
        const char *channel, const idsc_BinaryBlob *msg, void *userdata){
	printf("received path message\n");
	memcpy((int8_t*)&lastPathMsg, msg->data, msg->data_length);
	for (int i = 0; i<POINTSN; i++)
	{
		printf("i=%d: pointX:%f\n",i,lastPathMsg.path.controlPointsX[i]);
		printf("i=%d: pointX:%f\n",i,lastPathMsg.path.controlPointsY[i]);
		printf("i=%d: pointX:%f\n",i,lastPathMsg.path.controlPointsR[i]);
	}
}

static void state_handler(const lcm_recv_buf_t *rbuf,
        const char *channel, const idsc_BinaryBlob *msg, void *userdata){
	printf("received state message\n");
	memcpy((int8_t*)&lastStateMsg, msg->data, msg->data_length);

	struct MPCPathFollowing_params params;

	params.xinit[0] = lastStateMsg.state.X;
	params.xinit[1] = lastStateMsg.state.Y;
	params.xinit[2] = lastStateMsg.state.Psi;
	params.xinit[3] = lastStateMsg.state.Ux;
	params.xinit[4] = lastStateMsg.state.s;
	params.xinit[5] = lastPathMsg.path.startingProgress;

	//sendEmptyControlAndStates(lcm);
	/*
	struct _idsc_BinaryBlob blob;
	for (int i = 0; i<N; i++){
		cns[i].control.uL = 1;
		cns[i].control.uR = 2;
		cns[i].control.udotS = 3;
		cns[i].control.uB = 4;
		cns[i].state = stateMsg.state;
	}
	printf("prepared blob\n");
	blob.data_length = sizeof(struct ControlAndStateMsg)*N;
	blob.data = (int8_t*)&cns;
	printf("linked data\n");
	printf("lcm addr: %p\n",lcm);
	printf("blob addr: %p\n",&blob);
	printf("state Ux: %f\n",stateMsg.state.Ux);
	if(idsc_BinaryBlob_publish(lcm, "mpc.forces.cns", &blob)==0)
		printf("published message: %d\n",sizeof(struct ControlAndStateMsg)*N);
	else
		printf("error while publishing message\n");*/
}

int main(int argc, char *argv[]) {
	printf("start lcm server\n");
	lcm = lcm_create(NULL);
	if(!lcm)
		return 1;
	
	//return format [state]
	//exitflag = MPCPathFollowing_solve(&myparams, &myoutput, &myinfo, solverFile, pt2Function);
	
	//sendEmptyControlAndStates(lcm);
	printf("about to subscribe\n");
	idsc_BinaryBlob_subscribe(lcm, "mpc.forces.gs", &state_handler, NULL);
	idsc_BinaryBlob_subscribe(lcm, "mpc.forces.pp", &path_handler, NULL);
	idsc_BinaryBlob_subscribe(lcm, "mpc.forces.op", &para_handler, NULL);
	printf("starting main loop\n");
	while(1)
		lcm_handle(lcm);
	
	lcm_destroy(lcm);
	return 0;
}
