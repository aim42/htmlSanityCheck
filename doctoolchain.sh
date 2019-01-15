docker run --rm -it -v ${PWD}:/project rdmueller/doctoolchain:snapshot-0.1.10 \
-c "doctoolchain . $1 $2 $3 $4 $5 $6 $7 $8 $9 -PmainConfigFile=config/docToolchain.groovy && exit"


