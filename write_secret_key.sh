#!/bin/bash

cat <<EOF > secret_key.gpg
$GPG_SIGNING_KEY
EOF

md5sum secret_key.gpg
md5 secret_key.gpg

echo "Created secreted key file: Here it is: "
ls -la secret_key.gpg
cat secret_key.gpg