#!/bin/bash

# Copyright (c) 2014, ninckblokje
# All rights reserved.
# 
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
# 
# * Redistributions of source code must retain the above copyright notice, this
#   list of conditions and the following disclaimer.
# 
# * Redistributions in binary form must reproduce the above copyright notice,
#   this list of conditions and the following disclaimer in the documentation
#   and/or other materials provided with the distribution.
# 
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
# FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
# DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
# SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
# CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
# OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
# OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

# Wrapper script for the keytool command from the JDK. Written for more easy password input.
#
# Uses the following syntax: $ ./run_keytool.sh [KEYSTORE] [PASSWORD] {KEYTOOL_ARGUMENTS}
# Example: ./run_keytool.sh /opt/oracle/fmw/wlserver_10.3/server/lib/DemoTrust.jks DemoTrustKeyStorePassPhrase -list
#
# Make sure JAVA_HOME is set to a JDK.

if [[ "$JAVA_HOME" == "" ]]
then
	echo ERROR: Set JAVA_HOME
	exit 1
fi
if [[ "$1" == "" ]] || [[ "$2" == "" ]]
then
	echo Specify a KeyStore and a password
	exit 2
fi

keytoolCommand="$JAVA_HOME/bin/keytool ${@:3} -keystore $1"
echo Executing command: $keytoolCommand

$keytoolCommand <<EOF
$2
EOF
