#!/bin/bash

cat <<EOF > secret_key.gpg
$GPG_SIGNING_KEY
EOF

echo "Created secreted key file: Here it is: "
ls -la secret_key.gpg
cat secret_key.gpg