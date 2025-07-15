# Nginx Configuration for Fake-BRY

This nginx configuration serves as a reverse proxy and load balancer for the Fake-BRY application, handling both the React frontend and Spring Boot backend.

## Architecture

```
Internet → Nginx (Port 80/443) → Frontend (Port 3000) | Backend (Port 8080)
```

## Configuration Overview

### Frontend Routing

- **Root path (`/`)**: Serves the React application
- **Static assets**: Cached for 1 year with proper MIME types
- **SPA Support**: All unmatched routes fallback to React Router for client-side routing

### Backend API Routing

- **API path (`/api/*`)**: Proxied to Spring Boot backend
- **Rate limiting**: 10 requests/second with burst of 20
- **CORS enabled**: For cross-origin requests
- **Available endpoints**:
  - `GET /api/certificates/ca-info`
  - `GET /api/certificates/{serialNumber}`
  - `POST /api/certificates/issue-certificate`
  - `POST /api/certificates/validate-signature`

### Security Features

- Security headers (X-Frame-Options, XSS Protection, etc.)
- Content Security Policy
- CORS handling for API requests
- Rate limiting on API endpoints

### SSL/TLS Support

- Ready for SSL certificates (commented configuration)
- Listens on both HTTP (80) and HTTPS (443)
- Certificates should be mounted at `/certs/` volume

## Usage

The nginx service is accessible at:

- **HTTP**: `http://localhost` (port 80)
- **HTTPS**: `https://localhost` (port 443, when SSL is configured)

### Health Check

- **Endpoint**: `/health`
- **Response**: Simple "healthy" text response

## Development vs Production

In this setup:

- Frontend runs in production mode with built assets
- All requests go through nginx for unified access
- Static assets are properly cached
- API requests are proxied to the backend with proper headers

## Troubleshooting

1. **404 errors on page refresh**: The SPA fallback should handle this
2. **API calls failing**: Check CORS headers and backend connectivity
3. **Static assets not loading**: Verify MIME types and caching headers
