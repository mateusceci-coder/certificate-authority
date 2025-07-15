# Certificate Management

This directory contains the certificate generation service for the application.

## Structure

- `Dockerfile`: Contains instructions to build a lightweight Alpine-based container that generates SSL certificates
- Generated certificates are stored in a shared Docker volume

## Generated Files

The certificate service generates the following files:

- `rootCA.key`: Root Certificate Authority private key
- `rootCA.crt`: Root Certificate Authority certificate

## How it works

1. The `certificates` service builds and runs a container that generates all necessary SSL certificates
2. Certificates are stored in a Docker volume named `certs`
3. Both nginx and springboot services mount this volume as read-only
4. This approach separates certificate management from the nginx service, making it easier to manage and rotate certificates

## Certificate Rotation

To regenerate certificates:

1. Stop all services: `docker-compose down`
2. Remove the certificates volume: `docker volume rm fake-bry_certs`
3. Restart services: `docker-compose up --build`

## Customization

To customize certificate details, modify the `openssl req` commands in the Dockerfile with your desired values:

- Country (C)
- State (ST)
- City (L)
- Organization (O)
- Common Name (CN)
