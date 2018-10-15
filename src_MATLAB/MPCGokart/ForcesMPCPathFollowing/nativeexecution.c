/* A simple server in the internet domain using TCP
   The port number is passed as an argument */
#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdbool.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>

#include "MPCPathFollowing/include/MPCPathFollowing.h"

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

void closeConnection(){
    printf("Connection closed\n");
    close(sock);
    close(listen_sock);
    fclose(file);
    fclose(solverFile);
    finished = true;
    exit(0);
}

void intHandler(int dummy) {
	printf("finished after interuption\n");
	fprintf(file, "finished after interuption\n");
	closeConnection();
}

int main(int argc, char *argv[]) {
   	signal(SIGINT, intHandler);
	// port to start the server on
	int SERVER_PORT = 4143;

	printf("Server started\n");

        file = fopen("livesign.txt", "w"); // write only s
	fprintf(file, "Native server started\n");

    solverFile = fopen("solverLog.txt","w");
	// socket address used for the server
	struct sockaddr_in server_address;
	memset(&server_address, 0, sizeof(server_address));
	server_address.sin_family = AF_INET;

	// htons: host to network short: transforms a value in host byte
	// ordering format to a short value in network byte ordering format
	server_address.sin_port = htons(SERVER_PORT);

	// htonl: host to network long: same as htons but to long
	server_address.sin_addr.s_addr = htonl(INADDR_ANY);

	// create a TCP socket, creation returns -1 on failure
	if ((listen_sock = socket(PF_INET, SOCK_STREAM, 0)) < 0) {
		printf("could not create listen socket\n");
		return 1;
	}

	// bind it to listen to the incoming connections on the created server
	// address, will return -1 on error
	if ((bind(listen_sock, (struct sockaddr *)&server_address,
	          sizeof(server_address))) < 0) {
		printf("could not bind socket\n");
		return 1;
	}

	int wait_size = 16;  // maximum number of waiting clients, after which
	                     // dropping begins
	if (listen(listen_sock, wait_size) < 0) {
		printf("could not open socket for listening\n");
		return 1;
	}

	// socket address used to store client address
	struct sockaddr_in client_address;
	int client_address_len = 0;

	// run indefinitely
	//while (true) {
		// open a new socket to transmit data per connection
		if ((sock =
		         accept(listen_sock, (struct sockaddr *)&client_address,
		                &client_address_len)) < 0) {
			printf("could not open a socket to accept data\n");
			return 1;
		}
		if(finished)
			return 1;

		int n = 0;
		int len = 0, maxlen = 100;
		char buffer[maxlen];
		char *pbuffer = buffer;
		printf("client connected with ip address: %s\n",
		       inet_ntoa(client_address.sin_addr));

		fprintf(file, "client connected with ip address: %s\n",
	       		inet_ntoa(client_address.sin_addr));

		// keep running as long as the client keeps the connection open
		while ((n = recv(sock, pbuffer, maxlen, 0)) > 0) {
			//pbuffer += n;
			//maxlen -= n;
			//len += n;

			exitflag = MPCPathFollowing_solve(&myparams, &myoutput, &myinfo, solverFile, pt2Function);

			printf("received: '%s'\n", buffer);
			fprintf(file, "received: '%s'\n", buffer);

			// echo received content back
			send(sock, buffer, len, 0);
		}
    	fprintf(file, "Program closed after connection finished\n");
	closeConnection();
	return 0;
}
