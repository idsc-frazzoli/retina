/*!
  \example get_distance.c Obtains distance data
  \author Satofumi KAMIMURA

  $Id: get_distance.c,v c5747add6615 2015/05/07 03:18:34 alexandr $

  modified by
  \author datahaki

  code to obtain timestamp adapted from
  https://stackoverflow.com/questions/3756323/getting-the-current-time-in-milliseconds
*/

#include "urg_sensor.h"
#include "urg_utils.h"
#include "open_urg_sensor.h"
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <inttypes.h>
#include <math.h>
#include <time.h>

static void print_data(urg_t *urg, long data[], int data_n, long time_stamp) {
  printf("URH");
  {
    long   ms; // Milliseconds
    time_t s;  // Seconds
    struct timespec spec;
    // CLOCK_MONOTONIC vs. CLOCK_REALTIME
    // monotonic is not affected by user system time
    clock_gettime(CLOCK_MONOTONIC, &spec); 
    s  = spec.tv_sec;
    ms = round(spec.tv_nsec * 1e-6); // Convert nanoseconds to milliseconds
    printf("%"PRIdMAX"%03ld", (intmax_t)s, ms);
  }
  printf("{%ld", data[0]);
  for (int i = 1; i < data_n; ++i) 
    printf(",%ld", data[i]);
  printf("}\n");
}

int main(int argc, char *argv[]) {
  urg_t urg;
  long *data = NULL;
  long time_stamp;

  if (open_urg_sensor(&urg, argc, argv) < 0) 
    return 1;    

  data = (long *)malloc(urg_max_data_size(&urg) * sizeof(data[0]));
  if (!data) {
    perror("urg_max_index()");
    return 1;
  }

  fd_set readfds;
  FD_ZERO(&readfds);

  struct timeval timeout;
  timeout.tv_sec = 0;
  timeout.tv_usec = 0;
  char message[50];

#if 0
    // Case where the measurement range (start/end steps) is defined
    urg_set_scanning_parameter(&urg,
                               urg_deg2step(&urg, -90),
                               urg_deg2step(&urg, +90), 0);
#endif

  urg_start_measurement(&urg, URG_DISTANCE, URG_SCAN_INFINITY, 0);
  while (1) {
    int n = urg_get_distance(&urg, data, &time_stamp);
    if (n <= 0) {
      printf("urg_get_distance: %s\n", urg_error(&urg));
      free(data);
      urg_close(&urg);
      return 1;
    }
    print_data(&urg, data, n, time_stamp);

    FD_SET(STDIN_FILENO, &readfds);
    if (select(1, &readfds, NULL, NULL, &timeout)) {
      //printf("EXIT0\n");
      scanf("%s", message);
      //printf("EXIT1\n");
      break;
    }
    usleep(10);
  }

  //printf("EXIT2\n");
  free(data);
  urg_close(&urg);
  //printf("EXIT3\n");
  return 0;
}
