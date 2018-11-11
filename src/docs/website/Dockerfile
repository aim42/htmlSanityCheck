FROM jekyll/builder

LABEL version="0.8.0"
LABEL description="Developing the HtmlSanityChecker Website"
LABEL vendor="aim42"


COPY Gemfile .
# COPY Gemfile.lock .

RUN apk update && \
    chmod a+w /srv/jekyll/ && \
    bundle install

WORKDIR /srv/jekyll
EXPOSE 4000

CMD ["bash"]
