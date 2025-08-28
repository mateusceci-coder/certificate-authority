FROM alpine:latest

RUN apk add --no-cache openssl

RUN mkdir -p /certs

WORKDIR /certs

RUN openssl genrsa -out rootCA.key 2048

RUN openssl req -x509 -new -nodes -key rootCA.key -sha256 -days 3650 -out rootCA.crt \
    -subj "/C=BR/ST=SP/L=São Paulo/O=Fake BRy Organization/OU=IT Department/CN=Fake BRy Root CA"

RUN chmod 600 rootCA.key    
RUN chmod 644 rootCA.crt