docker run --rm --entrypoint /bin/bash -it -v ${PWD}:/project rdmueller/doctoolchain:rc-1.0.0 \
-c "doctoolchain . $1 $2 $3 $4 $5 $6 $7 $8 $9 -PmainConfigFile=config/docToolchain.groovy && exit"


