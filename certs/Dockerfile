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