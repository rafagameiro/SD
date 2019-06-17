#!/bin/bash

rm -f *.ks
rm -f *.cert

keytool -genkey -alias microgram -keyalg RSA -validity 365 -keystore microgram.ks -storetype pkcs12 << EOF
password
password
Rafael Gameiro
Thinkpad
FCT
Lisbon
Caparica
PT
yes
EOF

keytool -genkey -alias media -keyalg RSA -validity 365 -keystore media.ks -storetype pkcs12 << EOF
password
password
Ana Matos
Lenovo
FCT
Lisbon
Caparica
PT
yes
EOF

keytool -exportcert -alias microgram -keystore microgram.ks -file microgram.cert << EOF
password
yes
EOF

keytool -exportcert -alias media -keystore media.ks -file media.cert << EOF
password
yes
EOF

cp base-truststore.ks.proto tester-ts.ks
keytool -storepasswd -keystore tester-ts.ks << EOF
changeit
password
password
EOF

keytool -importcert -file microgram.cert  -alias microgram -keystore tester-ts.ks << EOF
password
yes
EOF

keytool -importcert -file media.cert -alias media -keystore tester-ts.ks << EOF
password
yes
EOF

rm -f *.cert
