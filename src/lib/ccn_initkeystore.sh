#!/bin/sh
# Create a ccn keystore without relying on java
: ${RSA_KEYSIZE:=1024}
: ${CCN_USER:=$USER}
Fail () {
  echo '*** Failed' "$*"
  exit 1
}
test -d .ccn && rm -rf .ccn
test $RSA_KEYSIZE -ge 512 || Fail \$RSA_KEYSIZE too small to sign CCN content
(umask 077 && mkdir .ccn) || Fail $0 Unable to create .ccn directory
cd .ccn
umask 077
trap 'rm -f *.pem openssl.cnf' 0
cat <<EOF >openssl.cnf
# This is not really relevant because we're not sending cert requests anywhere,
# but openssl req can refuse to go on if it has no config file.
[ req ]
distinguished_name	= req_distinguished_name
[ req_distinguished_name ]
countryName			= Country Name (2 letter code)
countryName_default		= AU
countryName_min			= 2
countryName_max			= 2
EOF
openssl req -config openssl.cnf -newkey rsa:$RSA_KEYSIZE -x509 -keyout private_key.pem -out certout.pem -subj /CN="$CCN_USER" -nodes || Fail openssl req
openssl pkcs12 -export -name "CCNUser" -out .ccn_keystore -in certout.pem -inkey private_key.pem \
  -password pass:'Th1s1sn0t8g00dp8ssw0rd.' || Fail openssl pkcs12

