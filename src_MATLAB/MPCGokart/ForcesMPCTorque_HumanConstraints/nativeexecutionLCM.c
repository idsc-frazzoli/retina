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

#include "MPCPathFollowing/include/MPCPathFollowing.h"
#include <lcm/lcm.h>
#include "../../../src_c/idsc/idsc_BinaryBlob.c"
#include "../shared_dynamic/c/definitions_torque.c"
#include "../shared_dynamic/c/helperFunctions.c"
#include <unistd.h>

// [dotab,dottau,ds,tv,slack,x,y,theta,dottheta,v,yv,ab,beta,s,dotbeta,tau]
#define N 31
#define S 16
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
MPCPathFollowing_output myoutput;
MPCPathFollowing_info myinfo;
MPCPathFollowing_float minusA_times_x0[2];
MPCPathFollowing_float lastInitialPsi = 0;

int outC = 0;

double timeOfLastSolution = -100;
double timeTolerance = 1;
MPCPathFollowing_float lastSolution [S*N];
struct ControlRequestMsg lastCRMsg;
struct ParaMsg lastParaMsg;

/* extern void MPCPathFollowing_casadi2forces(double *x, double *y, double *l, double *p,
                                                double *f, double *nabla_f, double *c, double *nabla_c,
                                                double *h, double *nabla_h, double *H, int stage); */


extern void MPCPathFollowing_casadi2forces(MPCPathFollowing_float* x, MPCPathFollowing_float* y, MPCPathFollowing_float* lambda, MPCPathFollowing_float* params, MPCPathFollowing_float* pobj, MPCPathFollowing_float* g, MPCPathFollowing_float* c, MPCPathFollowing_float* Jeq, MPCPathFollowing_float* h, MPCPathFollowing_float* Jineq, MPCPathFollowing_float* H, solver_int32_default stage, solver_int32_default iterations);

MPCPathFollowing_extfunc pt2Function =&MPCPathFollowing_casadi2forces;

// [dotab,dottau,ds,tv,slack,x,y,theta,dottheta,v,yv,ab,beta,s,dotbeta,tau]
static void getLastControls(
        MPCPathFollowing_float* ab,
        MPCPathFollowing_float* dotab,
        MPCPathFollowing_float* tau,
        MPCPathFollowing_float* dottau,
        double* dStepTime,
        double time) {
	double lastSolutionTime = timeOfLastSolution;
	double dTime = time-lastSolutionTime;
	int lastStep = (int)floor((time-lastSolutionTime)/ISS);
	*dStepTime = dTime - lastStep*ISS;
	// printf("timeval: %f\n",time);
	// printf("last step: %d/dtime %f\n",lastStep,*dStepTime);
	*ab = lastSolution[i*S + 11];
	*dotab = lastSolution[i*S];
	*tau = lastSolution[i*S + 15];
	*dottau = lastSolution[i*S + 1];
}

static void para_handler(const lcm_recv_buf_t *rbuf,
        const char *channel, const idsc_BinaryBlob *msg, void *userdata){
	printf("received path message\n");
	memcpy((int8_t*)&lastParaMsg, msg->data, msg->data_length);
	// printf("max speed: %f\n",lastParaMsg.para.speedLimit);
	// printf("max X-acc: %f\n",lastParaMsg.para.maxxacc);
	// printf("max Y-acc: %f\n",lastParaMsg.para.maxyacc);
	// printf("max front lat acc: %f\n",lastParaMsg.para.latacclim);
	// printf("max rot acc: %f\n",lastParaMsg.para.rotacceffect);
	// printf("torque vec effect: %f\n",lastParaMsg.para.torqueveceffect);
	// printf("brake effect: %f\n",lastParaMsg.para.brakeeffect);
}

static void state_handler(const lcm_recv_buf_t *rbuf,
        const char *channel, const idsc_BinaryBlob *msg, void *userdata){
	// printf("received control message\n");
	memcpy((int8_t*)&lastCRMsg, msg->data, msg->data_length);
    /* for (int i = 0; i < POINTSN; i++) {
    		struct PathEntry pe = lastCRMsg.path.controlPoints[i];
		printf("i=%d: pointX:%f\n",i,pe.pex);
		printf("i=%d: pointY:%f\n",i,pe.pey);
		printf("i=%d: pointR:%f\n",i,pe.per);
	} */

	MPCPathFollowing_params params;

	MPCPathFollowing_float lab;
	MPCPathFollowing_float ldotab;
	MPCPathFollowing_float ltau;
	MPCPathFollowing_float ldottau;
	double dTime;

	MPCPathFollowing_float initab;
	MPCPathFollowing_float inittau;

	if (lastCRMsg.state.time-timeOfLastSolution<timeTolerance) {
		getLastControls(
			&lab,
			&ldotab,
			&ltau,
			&ldottau,
			&dTime,
			lastCRMsg.state.time);

		initab = getInitAB(lab, ldotab, lastCRMsg.state.Ux, dTime);
		inittau = getInitTau(ltau, ldottau, dTime);
	} else
	    initab = 0;

	// [x,y,theta,dottheta,v,yv,beta,dotbeta,ab,tau,s]
	inittau = lastCRMsg.state.tau;
	params.xinit[0] = lastCRMsg.state.X+cos(lastCRMsg.state.Psi)*backToCoM;
	params.xinit[1] = lastCRMsg.state.Y+sin(lastCRMsg.state.Psi)*backToCoM;
	params.xinit[2] = lastCRMsg.state.Psi;
	params.xinit[3] = lastCRMsg.state.dotPsi;
	params.xinit[4] = lastCRMsg.state.Ux;
	params.xinit[5] = lastCRMsg.state.Uy+lastCRMsg.state.dotPsi*backToCoM;
	params.xinit[6] = initab;
	params.xinit[7] = lastCRMsg.state.s;
	params.xinit[8] = lastCRMsg.path.startingProgress;
	params.xinit[9] = lastCRMsg.state.dotbeta;
	params.xinit[10] = inittau;
	

	/* for(int i = 0; i<7;i++)
		printf("%i: %f\n",i,params.xinit[i]); */

	// gather parameter data
	int pl = 3*POINTSN+NUMPARAM;

	printf("parameters\n");
	for (int i = 0; i < N; i++){
		params.all_parameters[i*pl] = lastParaMsg.para.speedLimit;
		params.all_parameters[i*pl + 1] = lastParaMsg.para.maxxacc;
		params.all_parameters[i*pl + 2] = lastParaMsg.para.steeringreg;
		params.all_parameters[i*pl + 3] = lastParaMsg.para.specificmoi;
		params.all_parameters[i*pl + 4] = lastParaMsg.para.pacejkaFB;
		params.all_parameters[i*pl + 5] = lastParaMsg.para.pacejkaFC;
		params.all_parameters[i*pl + 6] = lastParaMsg.para.pacejkaFD;
		params.all_parameters[i*pl + 7] = lastParaMsg.para.pacejkaRB;
		params.all_parameters[i*pl + 8] = lastParaMsg.para.pacejkaRC;
		params.all_parameters[i*pl + 9] = lastParaMsg.para.pacejkaRD;
		params.all_parameters[i*pl + 10] = lastParaMsg.para.steerStiff;
		params.all_parameters[i*pl + 11] = lastParaMsg.para.steerDamp;
		params.all_parameters[i*pl + 12] = lastParaMsg.para.steerInertia;
		params.all_parameters[i*pl + 13] = lastParaMsg.para.lagError;
		params.all_parameters[i*pl + 14] = lastParaMsg.para.latError;
		params.all_parameters[i*pl + 15] = lastParaMsg.para.progress;
		params.all_parameters[i*pl + 16] = lastParaMsg.para.regularizerAB;
		params.all_parameters[i*pl + 17] = lastParaMsg.para.speedCost;
		params.all_parameters[i*pl + 18] = lastParaMsg.para.slackSoftConstraints;
		params.all_parameters[i*pl + 19] = lastParaMsg.para.regularizerTV;
		params.all_parameters[i*pl + 20] = lastParaMsg.para.regTorque;
		for (int ip = 0; ip < POINTSN; ip++)
			params.all_parameters[i*pl + NUMPARAM + ip] = lastCRMsg.path.controlPoints[ip].pex;
		for (int ip = 0; ip < POINTSN; ip++)
			params.all_parameters[i*pl + NUMPARAM + POINTSN + ip] = lastCRMsg.path.controlPoints[ip].pey;
		for (int ip = 0; ip < POINTSN; ip++)
			params.all_parameters[i*pl + NUMPARAM + 2*POINTSN + ip] = lastCRMsg.path.controlPoints[ip].per;
	}

	// assume that this works
	for (int i = 0; i < N*pl; i++)
		printf("i=%d: %f\n", i, params.all_parameters[i]);

	memcpy(params.x0, lastSolution, sizeof(MPCPathFollowing_float)*S*N);
	// TODO MH fix for 2PI wrap around problem: change initial guess according
	// change amount:
	MPCPathFollowing_float deltaPsi = lastCRMsg.state.Psi - lastInitialPsi;
	// printf("deltaPsi %f", deltaPsi);
	for(int i = 0; i < N; i++)
		params.x0[i*S + 7] += deltaPsi;
	lastInitialPsi = lastCRMsg.state.Psi;


	// do optimization
	exitflag = MPCPathFollowing_solve(&params, &myoutput, &myinfo, stdout, pt2Function);
	// look at data
	// optimal or maxit (maxit is ok in most cases)
	if(exitflag == 1 || exitflag == 0){
		memcpy(lastSolution, myoutput.alldata, sizeof(MPCPathFollowing_float)*S*N);
		//printf("lastSolution: %f\n", lastSolution[341]);
		timeOfLastSolution = lastCRMsg.state.time;

		// [dotab,dotbeta,ds,tv,slack,x,y,theta,dottheta,v,yv,ab,beta,s]

		struct ControlAndStateMsg cnsmsg;
		cnsmsg.messageType = 3;
		cnsmsg.sequenceInt = outC++;
		for (int i = 0; i < N; i++){
			MPCPathFollowing_float ab = myoutput.alldata[i*S + 11];
			MPCPathFollowing_float tv = myoutput.alldata[i*S + 3];
			MPCPathFollowing_float psi = myoutput.alldata[i*S + 7];
			MPCPathFollowing_float dotPsi = myoutput.alldata[i*S + 8];
			cnsmsg.cns[i].control.uL = ab - tv;
			cnsmsg.cns[i].control.uR = ab + tv;
			cnsmsg.cns[i].control.udotS = myoutput.alldata[i*S + 14];
			cnsmsg.cns[i].control.uB = 0; // not in use
			cnsmsg.cns[i].control.aB = ab;
			cnsmsg.cns[i].control.udotT = myoutput.alldata[i*S + 1];
			cnsmsg.cns[i].state.time = i*ISS + lastCRMsg.state.time;
			cnsmsg.cns[i].state.Ux = myoutput.alldata[i*S + 9];
			cnsmsg.cns[i].state.Uy = myoutput.alldata[i*S + 10]-lastCRMsg.state.dotPsi*backToCoM;
			// printf("pos: %f/%f rot: %f prog: %f dprog: %f\n",myoutput.alldata[i*S+4],myoutput.alldata[i*S+5],myoutput.alldata[i*S+6],myoutput.alldata[i*S+10],myoutput.alldata[i*S+2]);
			cnsmsg.cns[i].state.dotPsi = dotPsi;
			cnsmsg.cns[i].state.X = myoutput.alldata[i*S + 5] - cos(psi)*backToCoM;
			cnsmsg.cns[i].state.Y = myoutput.alldata[i*S + 6] - sin(psi)*backToCoM;
			cnsmsg.cns[i].state.Psi = psi;
			cnsmsg.cns[i].state.w2L = 0; // not in use
			cnsmsg.cns[i].state.w2R = 0; // not in use
			cnsmsg.cns[i].state.s = myoutput.alldata[i*S + 12];
			cnsmsg.cns[i].state.bTemp = 60;
			cnsmsg.cns[i].state.tau = myoutput.alldata[i*S + 15];
			cnsmsg.cns[i].state.dotbeta = myoutput.alldata[i*S + 14];
		}

		// printf("prepared blob\n");
		struct _idsc_BinaryBlob blob;
		blob.data_length = sizeof(struct ControlAndStateMsg);
		blob.data = (int8_t*)&cnsmsg;
		// printf("lcm addr: %p\n",lcm);
		// printf("blob addr: %p\n",&blob);
		// printf("sleep...");
		// usleep(50000);
		if (idsc_BinaryBlob_publish(lcm, "mpc.forces.cns", &blob) == 0)
			printf("published message: %lu\n", sizeof(struct ControlAndStateMsg));
		else
			printf("error while publishing message\n");
	} else
		printf("exitflag: %d\n", exitflag);
}

int main(int argc, char *argv[]) {
	printf("start lcm server\n");

	//for testing
	/* for(int i = -100; i < 100; i++){
		double v = i / 100.0;
		double maxacc = getMaxAcc(v);
		printf("%f: %f\n", v, maxacc);
	} */

	lcm = lcm_create(NULL);
	if (!lcm)
		return 1;

	// return format [state]
	// exitflag = MPCPathFollowing_solve(&myparams, &myoutput, &myinfo, solverFile, pt2Function);

	// sendEmptyControlAndStates(lcm);
	printf("about to subscribe\n");
	idsc_BinaryBlob_subscribe(lcm, "mpc.forces.gs.t", &state_handler, NULL);
	// idsc_BinaryBlob_subscribe(lcm, "mpc.forces.pp", &path_handler, NULL);
	idsc_BinaryBlob_subscribe(lcm, "mpc.forces.op.t", &para_handler, NULL);
	printf("starting main loop\n");
	while (1)
		lcm_handle(lcm);

	lcm_destroy(lcm);
	return 0;
}
