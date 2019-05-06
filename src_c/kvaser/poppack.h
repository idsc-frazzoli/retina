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

/*
** Description:
**   This file resets the struct alignment to the value
**   prevalent before pshpackN.h was included.  Many compilers provide a
**   file similar to this one.
** -----------------------------------------------------------------------------
*/

#if ! (defined(lint) || defined(_lint) || defined(RC_INVOKED))
#  if defined(_MSC_VER)
#    if ( _MSC_VER > 800 ) || defined(_PUSHPOP_SUPPORTED)
#      pragma warning(disable:4103)
#      if !(defined( MIDL_PASS )) || defined( __midl )
#        pragma pack(pop)
#      else
#        pragma pack()
#      endif
#    else
#      pragma warning(disable:4103)
#      pragma pack()
#    endif
#  elif defined(__C166__)
#    pragma pack()
#  elif defined(__LCC__)
#    pragma pack(pop)
#  elif defined(__MWERKS__)
#    pragma pack(pop)
#  elif defined(__BORLANDC__)
#    if (__BORLANDC__ >= 0x460)
#      pragma nopackwarning
#      pragma pack(pop)
#    else
#      pragma option -a.
#    endif
#  elif defined (__IAR_SYSTEMS_ICC)
     /* IAR M16C 1.x */
#    pragma alignment()
#  elif defined (__IAR_SYSTEMS_ICC__)
     /* IAR M32C 2.x */
#    pragma pack(pop)
#  elif defined (__GNUC__) && defined(__m32c__)
     /* gcc 4.x m32c */
#    pragma align reset
#  elif defined(__GNUC__) && defined(__linux__)
#    pragma pack()
#  elif defined (__GNUC__) && defined(__arm__) && defined(__ARM_ARCH_4T__)
     /* Do nothing */
#  elif defined(__GCC__)
#    pragma pack(pop)
#  else
#    error Unsupported compiler.
#  endif
#endif /* ! (defined(lint) || defined(_lint) || defined(RC_INVOKED)) */
