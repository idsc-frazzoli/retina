/* A simple server in the internet domain using TCP
   The port number is passed as an argument */
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <byteswap.h>
#include <math.h>

#include "OnlineMPCPathFollowing/include/OnlineMPCPathFollowing.h"
#include <lcm/lcm.h>
#include "../../../src_c/idsc/idsc_BinaryBlob.c"
#include "../shared_dynamic/c/definitions.c"
#include "helperFunctionsOnlineMPC.c"
#include <unistd.h>

//[dotab,dotbeta,ds,tv,slack,x,y,theta,dottheta,v,yv,ab,beta,s]

#define N 31
#define S 14
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

double backToCoM = 0.46;

#define DATASIZE 100
struct ControlAndStateMsg cns [DATASIZE];

/* declare FORCES variables and structures */
int i, exitflag;
OnlineMPCPathFollowing_output myoutput;
OnlineMPCPathFollowing_info myinfo;
OnlineMPCPathFollowing_float minusA_times_x0[2];
OnlineMPCPathFollowing_float lastInitialPsi = 0;

int outC = 0;

double timeOfLastSolution = -100;
double timeTolerance = 1;
OnlineMPCPathFollowing_float lastSolution [S*N];
struct ControlRequestMsg lastCRMsg;
struct ParaMsg lastParaMsg;
struct PacejkaParameter lastPacjMsg;

extern void OnlineMPCPathFollowing_casadi2forces(
        OnlineMPCPathFollowing_float* x,
        OnlineMPCPathFollowing_float* y,
        OnlineMPCPathFollowing_float* lambda,
        OnlineMPCPathFollowing_float* params,
        OnlineMPCPathFollowing_float* pobj,
        OnlineMPCPathFollowing_float* g,
        OnlineMPCPathFollowing_float* c,
        OnlineMPCPathFollowing_float* Jeq,
        OnlineMPCPathFollowing_float* h,
        OnlineMPCPathFollowing_float* Jineq,
        OnlineMPCPathFollowing_float* H,
        solver_int32_default stage,
        solver_int32_default iterations);

OnlineMPCPathFollowing_extfunc pt2Function =&OnlineMPCPathFollowing_casadi2forces;

//[dotab,dotbeta,ds,tv,slack,x,y,theta,dottheta,v,yv,ab,beta,s]
static void getLastControls(
        OnlineMPCPathFollowing_float* ab,
        OnlineMPCPathFollowing_float* dotab,
        OnlineMPCPathFollowing_float* beta,
        OnlineMPCPathFollowing_float* dotbeta,
	double* dStepTime,
	double time){
	double lastSolutionTime = timeOfLastSolution;
	double dTime = time-lastSolutionTime;
	int lastStep = (int)floor((time-lastSolutionTime)/ISS);
	*dStepTime = dTime - lastStep*ISS;
	//printf("timeval: %f\n",time);
	//printf("last step: %d/dtime %f\n",lastStep,*dStepTime);
	*ab = lastSolution[i*S+11];
	*dotab = lastSolution[i*S];
	*beta = lastSolution[i*S+12];
	*dotbeta = lastSolution[i*S+1];
}

static void para_handler(const lcm_recv_buf_t *rbuf,
                         const char *channel, const idsc_BinaryBlob *msg, void *userdata){
    printf("received path message\n");
    memcpy((int8_t*)&lastParaMsg, msg->data, msg->data_length);
    //printf("max speed: %f\n",lastParaMsg.para.speedLimit);
    //printf("max X-acc: %f\n",lastParaMsg.para.maxxacc);
    //printf("max Y-acc: %f\n",lastParaMsg.para.maxyacc);
    //printf("max front lat acc: %f\n",lastParaMsg.para.latacclim);
    //printf("max rot acc: %f\n",lastParaMsg.para.rotacceffect);
    //printf("torque vec effect: %f\n",lastParaMsg.para.torqueveceffect);
    //printf("brake effect: %f\n",lastParaMsg.para.brakeeffect);
}

static void pacj_handler(const lcm_recv_buf_t *rbuf,
                         const char *channel, const idsc_BinaryBlob *msg, void *userdata){
    printf("received pacj message\n");
    memcpy((int8_t*)&lastPacjMsg, msg->data, msg->data_length);
    printf("B1: %f\n",lastPacjMsg.B1);
    printf("C1: %f\n",lastPacjMsg.C1);
    printf("D1: %f\n",lastPacjMsg.D1);
    printf("B2: %f\n",lastPacjMsg.B2);
    printf("C2: %f\n",lastPacjMsg.C2);
    printf("D2: %f\n",lastPacjMsg.D2);
}

static void state_handler(const lcm_recv_buf_t *rbuf,
        const char *channel, const idsc_BinaryBlob *msg, void *userdata){
	//printf("received control message\n");
	memcpy((int8_t*)&lastCRMsg, msg->data, msg->data_length);
  /*for (int i = 0; i<POINTSN; i++)
	{
    		struct PathEntry pe = lastCRMsg.path.controlPoints[i];
		printf("i=%d: pointX:%f\n",i,pe.pex);
		printf("i=%d: pointY:%f\n",i,pe.pey);
		printf("i=%d: pointR:%f\n",i,pe.per);
	}*/

    OnlineMPCPathFollowing_params params;

    OnlineMPCPathFollowing_float lab;
    OnlineMPCPathFollowing_float ldotab;
    OnlineMPCPathFollowing_float lbeta;
    OnlineMPCPathFollowing_float ldotbeta;
	double dTime;

    OnlineMPCPathFollowing_float initab;
    OnlineMPCPathFollowing_float initbeta;

	if(lastCRMsg.state.time-timeOfLastSolution<timeTolerance){
		getLastControls(
			&lab,
			&ldotab,
			&lbeta,
			&ldotbeta,
			&dTime,
			lastCRMsg.state.time);
		
		initab = getInitAB(lab, ldotab, lastCRMsg.state.Ux, dTime);
		initbeta = getInitSteer(lbeta, ldotbeta, dTime);
	}else
	{
		initab = 0;

	}
	//[x,y,theta,dottheta,v,yv,ab,beta,s]
	initbeta = lastCRMsg.state.s;
	params.xinit[0] = lastCRMsg.state.X+cos(lastCRMsg.state.Psi)*backToCoM;
	params.xinit[1] = lastCRMsg.state.Y+sin(lastCRMsg.state.Psi)*backToCoM;
	params.xinit[2] = lastCRMsg.state.Psi;
	params.xinit[3] = lastCRMsg.state.dotPsi;
	params.xinit[4] = lastCRMsg.state.Ux;
	params.xinit[5] = lastCRMsg.state.Uy+lastCRMsg.state.dotPsi*backToCoM;
	params.xinit[6] = initab;
	params.xinit[7] = initbeta;
	params.xinit[8] = lastCRMsg.path.startingProgress;

	/*for(int i = 0; i<7;i++){
		printf("%i: %f\n",i,params.xinit[i]);
	}*/

	//gather parameter data
	int pl = 3*POINTSN+4 + 1;
	
	printf("parameters\n");
	for(int i = 0; i<N;i++){
	    int offset = i*pl;
		params.all_parameters[offset] = lastParaMsg.para.speedLimit;
		params.all_parameters[offset] = lastParaMsg.para.maxxacc;
		params.all_parameters[offset] = lastParaMsg.para.steeringreg;
		params.all_parameters[offset+3] = lastParaMsg.para.specificmoi;
		for (int ip=0; ip<POINTSN;ip++)
			params.all_parameters[offset+4+ip]=lastCRMsg.path.controlPoints[ip].pex;
		for (int ip=0; ip<POINTSN;ip++)
			params.all_parameters[offset+4+POINTSN+ip]=lastCRMsg.path.controlPoints[ip].pey;
		for (int ip=0; ip<POINTSN;ip++)
			params.all_parameters[offset+4+2*POINTSN+ip]=lastCRMsg.path.controlPoints[ip].per;
		params.all_parameters[offset+4+3*POINTSN+1] = lastPacjMsg.B1;
	}
	
	//assume that this works
	for(int i = 0; i<N*(4+POINTSN*3+1);i++)
		printf("i=%d: %f\n",i,params.all_parameters[i]);

	memcpy(params.x0, lastSolution,sizeof(OnlineMPCPathFollowing_float)*S*N);
	// TODO MH fix for 2PI wrap around problem: change initial guess according
	//change amount:
    OnlineMPCPathFollowing_float deltaPsi = lastCRMsg.state.Psi-lastInitialPsi;
	//printf("deltaPsi %f", deltaPsi);
	for(int i = 0; i<N;i++){
		params.x0[i*S+7]+=deltaPsi;
	}
	lastInitialPsi = lastCRMsg.state.Psi;


	//do optimization
	exitflag = OnlineMPCPathFollowing_solve(&params, &myoutput, &myinfo, stdout, pt2Function);
	//look at data
	//optimal or maxit (maxit is ok in most cases)
	if(exitflag == 1 || exitflag == 0){
		memcpy(lastSolution, myoutput.alldata,sizeof(OnlineMPCPathFollowing_float)*S*N);
		//printf("lastSolution: %f\n", lastSolution[341]);
		timeOfLastSolution = lastCRMsg.state.time;

		//[dotab,dotbeta,ds,tv,slack,x,y,theta,dottheta,v,yv,ab,beta,s]
		
		struct ControlAndStateMsg cnsmsg;
		cnsmsg.messageType = 3;
		cnsmsg.sequenceInt = outC++;
		for(int i = 0; i<N; i++){
            OnlineMPCPathFollowing_float ab = myoutput.alldata[i*S+11];
            OnlineMPCPathFollowing_float tv = myoutput.alldata[i*S+3];
            OnlineMPCPathFollowing_float psi = myoutput.alldata[i*S+7];
            OnlineMPCPathFollowing_float dotPsi = myoutput.alldata[i*S+8];
			cnsmsg.cns[i].control.uL = ab-tv;
			cnsmsg.cns[i].control.uR = ab+tv;
			cnsmsg.cns[i].control.udotS = myoutput.alldata[i*S+1];
			cnsmsg.cns[i].control.uB = 0;//not in use
			cnsmsg.cns[i].control.aB = ab;
			cnsmsg.cns[i].state.time = i*ISS+lastCRMsg.state.time;
			cnsmsg.cns[i].state.Ux = myoutput.alldata[i*S+9];
			cnsmsg.cns[i].state.Uy = myoutput.alldata[i*S+10]-lastCRMsg.state.dotPsi*backToCoM;
			//printf("pos: %f/%f rot: %f prog: %f dprog: %f\n",myoutput.alldata[i*S+4],myoutput.alldata[i*S+5],myoutput.alldata[i*S+6],myoutput.alldata[i*S+10],myoutput.alldata[i*S+2]);
			cnsmsg.cns[i].state.dotPsi = dotPsi;
			cnsmsg.cns[i].state.X = myoutput.alldata[i*S+5]-cos(psi)*backToCoM;
			cnsmsg.cns[i].state.Y = myoutput.alldata[i*S+6]-sin(psi)*backToCoM;
			cnsmsg.cns[i].state.Psi = psi;
			cnsmsg.cns[i].state.w2L = 0;//not in use
			cnsmsg.cns[i].state.w2R = 0;//not in use
			cnsmsg.cns[i].state.s = myoutput.alldata[i*S+12];
			cnsmsg.cns[i].state.bTemp = 60;
		}

		//printf("prepared blob\n");
		struct _idsc_BinaryBlob blob;
		blob.data_length = sizeof(struct ControlAndStateMsg);
		blob.data = (int8_t*)&cnsmsg;
		//printf("lcm addr: %p\n",lcm);
		//printf("blob addr: %p\n",&blob);
		//printf("sleep...");	
		//usleep(50000);
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

    lastPacjMsg.B1 = 9;
    lastPacjMsg.C1 = 1;
    lastPacjMsg.D1 = 10;
    lastPacjMsg.B2 = 5.2;
    lastPacjMsg.C2 = 1.1;
    lastPacjMsg.D2 = 20;
	
	lcm = lcm_create(NULL);
	if(!lcm)
		return 1;
	
	//return format [state]
	//exitflag = OnlineMPCPathFollowing_solve(&myparams, &myoutput, &myinfo, solverFile, pt2Function);
	
	//sendEmptyControlAndStates(lcm);
	printf("about to subscribe\n");
    idsc_BinaryBlob_subscribe(lcm, "mpc.forces.pacj.d", &pacj_handler, NULL);
    idsc_BinaryBlob_subscribe(lcm, "mpc.forces.gs.d", &state_handler, NULL);
	idsc_BinaryBlob_subscribe(lcm, "mpc.forces.op.d", &para_handler, NULL);
	printf("starting main loop\n");
	while(1)
		lcm_handle(lcm);
	
	lcm_destroy(lcm);
	return 0;
}
