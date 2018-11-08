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
#define S 10
#define ISS 0.1
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
MPCPathFollowing_output myoutput;
MPCPathFollowing_info myinfo;
MPCPathFollowing_float minusA_times_x0[2];

int outC = 0;

MPCPathFollowing_float lastSolution [410];
struct ControlRequestMsg lastCRMsg;
struct ParaMsg lastParaMsg;

extern void MPCPathFollowing_casadi2forces(double *x, double *y, double *l, double *p,
                                                double *f, double *nabla_f, double *c, double *nabla_c,
                                                double *h, double *nabla_h, double *H, int stage);


MPCPathFollowing_extfunc pt2Function =&MPCPathFollowing_casadi2forces;



static void para_handler(const lcm_recv_buf_t *rbuf,
        const char *channel, const idsc_BinaryBlob *msg, void *userdata){
	printf("received path message\n");
	memcpy((int8_t*)&lastParaMsg, msg->data, msg->data_length);
	printf("max speed: %f\n",lastParaMsg.para.speedLimit);
}

static void state_handler(const lcm_recv_buf_t *rbuf,
        const char *channel, const idsc_BinaryBlob *msg, void *userdata){
	printf("received control message\n");
	memcpy((int8_t*)&lastCRMsg, msg->data, msg->data_length);

	for (int i = 0; i<POINTSN; i++)
	{
		printf("i=%d: pointX:%f\n",i,lastCRMsg.path.controlPointsX[i]);
		printf("i=%d: pointY:%f\n",i,lastCRMsg.path.controlPointsY[i]);
		printf("i=%d: pointR:%f\n",i,lastCRMsg.path.controlPointsR[i]);
	}

	struct MPCPathFollowing_params params;

	params.xinit[0] = lastCRMsg.state.X;
	params.xinit[1] = lastCRMsg.state.Y;
	params.xinit[2] = lastCRMsg.state.Psi;
	params.xinit[3] = lastCRMsg.state.Ux;
	params.xinit[4] = lastCRMsg.state.s;
	params.xinit[5] = lastCRMsg.path.startingProgress;
	params.xinit[6] = lastCRMsg.state.bTemp;

	for(int i = 0; i<7;i++){
		printf("%i: %f\n",i,params.xinit[i]);
	}

	//gather parameter data
	int pl = 2*POINTSN+1;
	
	printf("parameters\n");
	for(int i = 0; i<N;i++){
		params.all_parameters[i*pl] = lastParaMsg.para.speedLimit;
		for (int ip=0; ip<POINTSN;ip++)
			params.all_parameters[i*pl+1+ip]=lastCRMsg.path.controlPointsX[ip];
		for (int ip=0; ip<POINTSN;ip++)
			params.all_parameters[i*pl+1+POINTSN+ip]=lastCRMsg.path.controlPointsY[ip];
	}
	
	//assume that this works
	//for(int i = 0; i<31*20+1;i++)
	//	printf("i=%d: %f\n",i,params.all_parameters[i]);

	memcpy(params.x0, lastSolution,sizeof(MPCPathFollowing_float)*10*N);

	//do optimization
	exitflag = MPCPathFollowing_solve(&params, &myoutput, &myinfo, stdout, pt2Function);
	//look at data
	if(exitflag == 1 || exitflag == 0){
		memcpy(lastSolution, myoutput.alldata,sizeof(MPCPathFollowing_float)*10*N);	

		struct ControlAndStateMsg cnsmsg;
		cnsmsg.messageType = 3;
		cnsmsg.sequenceInt = outC++;
		for(int i = 0; i<N; i++){
			cnsmsg.cns[i].control.uL = 0;//not in use
			cnsmsg.cns[i].control.uR = 0;//not in use
			cnsmsg.cns[i].control.udotS = myoutput.alldata[i*S+1];
			cnsmsg.cns[i].control.uB = 0;//not in use
			cnsmsg.cns[i].control.aB = myoutput.alldata[i*S];
			cnsmsg.cns[i].state.time = i*ISS+lastCRMsg.state.time;
			cnsmsg.cns[i].state.Ux = myoutput.alldata[i*S+6];
			cnsmsg.cns[i].state.Uy = 0;//assumed = 0
			printf("pos: %f/%f rot: %f prog: %f dprog: %f\n",myoutput.alldata[i*S+3],myoutput.alldata[i*S+4],myoutput.alldata[i*S+5],myoutput.alldata[i*S+8],myoutput.alldata[i*S+2]);
			cnsmsg.cns[i].state.dotPsi = 0; //not in use
			cnsmsg.cns[i].state.X = myoutput.alldata[i*S+3];
			cnsmsg.cns[i].state.Y = myoutput.alldata[i*S+4];
			cnsmsg.cns[i].state.Psi = myoutput.alldata[i*S+5];
			cnsmsg.cns[i].state.w2L = 0;//not in use
			cnsmsg.cns[i].state.w2R = 0;//not in use
			cnsmsg.cns[i].state.s = myoutput.alldata[i*S+7];
			cnsmsg.cns[i].state.bTemp = myoutput.alldata[i*S+9];
		}

		printf("prepared blob\n");
		struct _idsc_BinaryBlob blob;
		blob.data_length = sizeof(struct ControlAndStateMsg);
		blob.data = (int8_t*)&cnsmsg;
		printf("lcm addr: %p\n",lcm);
		printf("blob addr: %p\n",&blob);
		if(idsc_BinaryBlob_publish(lcm, "mpc.forces.cns", &blob)==0)
			printf("published message: %lu\n",sizeof(struct ControlAndStateMsg));
		else
			printf("error while publishing message\n");
	}else{
		printf("exitflag: %d\n",exitflag);
	}
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
	//idsc_BinaryBlob_subscribe(lcm, "mpc.forces.pp", &path_handler, NULL);
	idsc_BinaryBlob_subscribe(lcm, "mpc.forces.op", &para_handler, NULL);
	printf("starting main loop\n");
	while(1)
		lcm_handle(lcm);
	
	lcm_destroy(lcm);
	return 0;
}
