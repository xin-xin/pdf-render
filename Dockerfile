FROM centos:centos7

MAINTAINER Xin Xin <xinx78@gmail.com>

RUN yum update -y
RUN yum install -y libreoffice-headless libreoffice-writer libreoffice-calc libreoffice- libreoffice-impress libreoffice-langpack-zh-Hans libreoffice-langpack-zh-Hant libreoffice-langpack-ja curl

RUN yum clean all

ADD ./target/pdf-render-1.0.jar ./
CMD java -jar pdf-render-1.0.jar /openoffice.info
