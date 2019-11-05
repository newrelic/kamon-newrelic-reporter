#!/bin/bash

cat <<EOF > credentials.properties
realm="Sonatype Nexus Repository Manager"
host=oss.sonatype.org
user=$MAVEN_PUBLISH_USERNAME
password=$MAVEN_PUBLISH_PASSWORD
EOF

echo "Created credentials.properties file: Here it is: "
ls -la credentials.properties