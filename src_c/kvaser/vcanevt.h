/*
**             Copyright 2017 by Kvaser AB, Molndal, Sweden
**                         http://www.kvaser.com
**
** This software is dual licensed under the following two licenses:
** BSD-new and GPLv2. You may use either one. See the included
** COPYING file for details.
**
** License: BSD-new
** ==============================================================================
** Redistribution and use in source and binary forms, with or without
** modification, are permitted provided that the following conditions are met:
**     * Redistributions of source code must retain the above copyright
**       notice, this list of conditions and the following disclaimer.
**     * Redistributions in binary form must reproduce the above copyright
**       notice, this list of conditions and the following disclaimer in the
**       documentation and/or other materials provided with the distribution.
**     * Neither the name of the <organization> nor the
**       names of its contributors may be used to endorse or promote products
**       derived from this software without specific prior written permission.
**
** THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
** AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
** IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
** ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
** LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
** CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
** SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
** BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
** IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
** ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
** POSSIBILITY OF SUCH DAMAGE.
**
**
** License: GPLv2
** ==============================================================================
** This program is free software; you can redistribute it and/or modify
** it under the terms of the GNU General Public License as published by
** the Free Software Foundation; either version 2 of the License, or
** (at your option) any later version.
**
** This program is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
** GNU General Public License for more details.
**
** You should have received a copy of the GNU General Public License
** along with this program; if not, write to the Free Software
** Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
**
**
** IMPORTANT NOTICE:
** ==============================================================================
** This source code is made available for free, as an open license, by Kvaser AB,
** for use with its applications. Kvaser AB does not accept any liability
** whatsoever for any third party patent or other immaterial property rights
** violations that may result from any usage of this source code, regardless of
** the combination of source code and various applications that it can be used
** in, or with.
**
** -----------------------------------------------------------------------------
*/

#ifndef VCANEVT_H
#define VCANEVT_H

#   if !defined(__KERNEL__)
#      include <stdint.h>
#   else
#      include <linux/types.h>
#   endif

/***************************************************************************/

#include <pshpack1.h>

/***************************************************************************/

enum e_vevent_type {
       V_NO_COMMAND            = 1,
       V_RECEIVE_MSG           = 2,
       V_STATISTIC_STD         = 4,
       V_STATISTIC_EXT         = 8,
       V_CHIP_STATE            = 16,
       V_TRANSCEIVER           = 32,
       V_TIMER                 = 64,
       V_TRANSMIT_MSG          = 128,
     };

typedef unsigned char VeventTag;


/* Structure for V_RECEIVE_MSG, V_TRANSMIT_MSG */

/* Message flags */
#define MAX_MSG_LEN                 128
#define EXT_MSG                     0x80000000 // signs an extended identifier
#define VCAN_EXT_MSG_ID             EXT_MSG
#define VCAN_MSG_ID_UNDEF           0U

#define VCAN_MSG_FLAG_ERROR_FRAME   0x01
#define VCAN_MSG_FLAG_OVERRUN       0x02  /* Overrun in Driver or CAN Controller */
                                          /* special case: OVERRUN combined with TIMER
                                           * means the 32 bit timer has overrun
                                           */
#define VCAN_MSG_FLAG_NERR          0x04  /* Line Error on Lowspeed */
#define VCAN_MSG_FLAG_WAKEUP        0x08  /* High Voltage Message on Single Wire CAN */
#define VCAN_MSG_FLAG_REMOTE_FRAME  0x10
#define VCAN_MSG_FLAG_RESERVED_1    0x20
#define VCAN_MSG_FLAG_TX_NOTIFY     0x40  /* Message Transmitted */
#define VCAN_MSG_FLAG_TXACK         0x40  /* Message Transmitted */
#define VCAN_MSG_FLAG_TX_START      0x80  /* Transmit Message stored into Controller  */
#define VCAN_MSG_FLAG_TXRQ          0x80  /* Transmit Message stored into Controller  */

#define VCAN_MSG_FLAG_EDL           0x100  /* Obsolete, use VCAN_MSG_FLAG_FDF instead */
#define VCAN_MSG_FLAG_FDF           0x100  /* Extended Data Length (CAN FD) */
#define VCAN_MSG_FLAG_BRS           0x200  /* Bit Rate Switch (CAN FD)  */
#define VCAN_MSG_FLAG_ESI           0x400  /* Error Status Indication (CAN FD)  */
#define VCAN_MSG_FLAG_SINGLE_SHOT   0x800  /* Send message as single shot (only tx) */
#define VCAN_MSG_FLAG_SSM_NACK     0x1000  /* Single shot failed (only rx)  */
#define VCAN_MSG_FLAG_SSM_NACK_ABL 0x2000  /* Single shot failed due to arbitration loss (only rx) */



/* Used for objbuf write, we need these since the brs flag above is made to fit in flags2 BRS */
#define VCAN_AUTOTX_MSG_FLAG_REMOTE_FRAME  0x10
#define VCAN_AUTOTX_MSG_FLAG_FDF           0x20
#define VCAN_AUTOTX_MSG_FLAG_BRS           0x40


struct s_vcan_msg {  /* 14 Bytes */
  uint32_t           id;
  unsigned short int flags;
  unsigned char      dlc;
  unsigned char      data[MAX_MSG_LEN];
};


/* Structure for V_CHIP_STATE */

#define CHIPSTAT_BUSOFF              0x01
#define CHIPSTAT_ERROR_PASSIVE       0x02
#define CHIPSTAT_ERROR_WARNING       0x04
#define CHIPSTAT_ERROR_ACTIVE        0x08

struct s_vcan_chip_state {
         unsigned char busStatus;
         unsigned char txErrorCounter;
         unsigned char rxErrorCounter;
       };


/* Structure for V_STATISTIC_STD */
struct s_vcan_statistic_std {
         uint32_t       stdData;
         uint32_t       stdRemote;
         uint32_t       errFrame;
         unsigned short busLoad; // 0.00-100.00%
       };


/* Structure for V_STATISTIC_EXT */
struct s_vcan_statistic_ext {
         uint32_t extData;
         uint32_t extRemote;
         uint32_t ovrFrame;
       };


/* Structure for V_ERROR */
struct s_vcan_error {
         unsigned char code;
       };


/* Structure for SET_OUTPUT_MODE */
#define OUTPUT_MODE_SILENT 0
#define OUTPUT_MODE_NORMAL 1


/* Transceiver modes */
#define TRANSCEIVER_EVENT_ERROR   1
#define TRANSCEIVER_EVENT_CHANGED 2



/* Transceiver modes */
#define VCAN_TRANSCEIVER_LINEMODE_NA            0 // Not Affected/Not available.
#define VCAN_TRANSCEIVER_LINEMODE_TWO_LINE      1 // W210 two-line.
#define VCAN_TRANSCEIVER_LINEMODE_CAN_H         2 // W210 single-line CAN_H
#define VCAN_TRANSCEIVER_LINEMODE_CAN_L         3 // W210 single-line CAN_L
#define VCAN_TRANSCEIVER_LINEMODE_SWC_SLEEP     4 // SWC Sleep Mode.
#define VCAN_TRANSCEIVER_LINEMODE_SWC_NORMAL    5 // SWC Normal Mode.
#define VCAN_TRANSCEIVER_LINEMODE_SWC_FAST      6 // SWC High-Speed Mode.
#define VCAN_TRANSCEIVER_LINEMODE_SWC_WAKEUP    7 // SWC Wakeup Mode.
#define VCAN_TRANSCEIVER_LINEMODE_SLEEP         8 // Sleep mode for those supporting it.
#define VCAN_TRANSCEIVER_LINEMODE_NORMAL        9 // Normal mode (the inverse of sleep mode) for those supporting it.
#define VCAN_TRANSCEIVER_LINEMODE_STDBY        10 // Standby for those who support it
#define VCAN_TRANSCEIVER_LINEMODE_TT_CAN_H     11 // Truck & Trailer: operating mode single wire using CAN high
#define VCAN_TRANSCEIVER_LINEMODE_TT_CAN_L     12 // Truck & Trailer: operating mode single wire using CAN low
#define VCAN_TRANSCEIVER_LINEMODE_OEM1         13 // Reserved for OEM apps
#define VCAN_TRANSCEIVER_LINEMODE_OEM2         14 // Reserved for OEM apps
#define VCAN_TRANSCEIVER_LINEMODE_OEM3         15 // Reserved for OEM apps
#define VCAN_TRANSCEIVER_LINEMODE_OEM4         16 // Reserved for OEM apps


#define VCAN_TRANSCEIVER_RESNET_NA              0
#define VCAN_TRANSCEIVER_RESNET_MASTER          1
#define VCAN_TRANSCEIVER_RESNET_MASTER_STBY     2
#define VCAN_TRANSCEIVER_RESNET_SLAVE           3


/* VCAN_EVENT structure */
union s_vcan_tag_data {
        struct s_vcan_msg                  msg;
        struct s_vcan_chip_state           chipState;
        struct s_vcan_statistic_std        statisticStd;
        struct s_vcan_statistic_ext        statisticExt;
        struct s_vcan_error                error;
      };


/* Event type definition */
struct s_vcan_event {
         VeventTag     tag;             // 1
         unsigned char chanIndex;       // 1
         unsigned char transId;         // 1
         unsigned char unused_1;        // 1 internal use only !!!!
         unsigned long timeStamp;       // 4 or 8
         union s_vcan_tag_data
                       tagData;         // 14 Bytes (_VMessage)
       };
                                        // --------
                                        // 22 or 26 Bytes

typedef struct s_vcan_event VCAN_EVENT, Vevent, *PVevent;


typedef struct s_can_msg {
  VeventTag          tag;
  unsigned char      channel_index;
  unsigned char      user_data;
  unsigned char      unused_1;
  unsigned long      timestamp;
  uint32_t           id;
  unsigned short int flags;
  unsigned char      length;
  unsigned char      data [MAX_MSG_LEN];
} CAN_MSG;


/*****************************************************************************/

#include <poppack.h>

/*****************************************************************************/

#endif /* VCANEVT_H */

