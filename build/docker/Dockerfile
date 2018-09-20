FROM docker-registry.jdddata.com/jdddata/apolloconfig-base:latest
ENV LANG zh_CN.UTF-8
RUN mkdir -p /usr/local/app/logs
WORKDIR /usr/local/app
ADD *-install.tar.gz /usr/local/app
EXPOSE 9999
EXPOSE 18080
CMD ["bin/start.sh"]
