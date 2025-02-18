FROM eclipse-temurin:21-jre-alpine

ARG DESCRIPTION='HSC (HTML Sanity Check) is a fast and lightweight tool for checking HTML, links, and accessibility issues.'
ARG VERSION=Unknown

LABEL version=${VERSION}
LABEL org.opencontainers.image.description=${DESCRIPTION}

COPY hsc.sh /hsc.sh
RUN chmod 755 /hsc.sh

COPY build/libs/htmlSanityCheck-cli-${VERSION}-all.jar /hsc.jar

ENTRYPOINT ["/hsc.sh"]