cat <<EOF
Command/Utility Documentation
=============================

EOF
grep '<title>.*</title>' "$@"   | tr A-Z a-z |\
    sed -e 's/^/ - link:/' -e 's/:[ 	]*<title>/\[/' -e 's/<.title>/\]/'
cat <<EOF

link:../index.html[Main Page]
//////////////////////////////////////
index.txt is generated by $0
//////////////////////////////////////
EOF
