FROM alpine:latest

# Install OpenSSL
RUN apk add --no-cache openssl

# Create directory for certificates
RUN mkdir -p /certs

# Set working directory
WORKDIR /certs

# Generate the Root CA private key
RUN openssl genrsa -out rootCA.key 2048

# Generate the Root CA certificate (self-signed)
RUN openssl req -x509 -new -nodes -key rootCA.key -sha256 -days 3650 -out rootCA.crt \
    -subj "/C=BR/ST=SP/L=São Paulo/O=Fake BRy Organization/OU=IT Department/CN=Fake BRy Root CA"

# Set appropriate permissions
RUN chmod 600 rootCA.key
RUN chmod 644 rootCA.crt

# Create a simple script to verify the certificate
RUN echo '#!/bin/sh' > verify.sh && \
    echo 'echo "Root CA Certificate Details:"' >> verify.sh && \
    echo 'openssl x509 -in rootCA.crt -text -noout' >> verify.sh && \
    chmod +x verify.sh

# Keep the container running (for docker-compose scenarios)
CMD ["tail", "-f", "/dev/null"]
