FROM node:20-alpine AS builder

WORKDIR /app

# Install deps first to leverage cache
COPY front/package*.json ./
RUN npm ci --only=production

# Build frontend
COPY front/ .
RUN npm run build

FROM nginx:alpine

# Copy production nginx config
COPY nginx/nginx.conf /etc/nginx/nginx.conf

# Copy built static assets
COPY --from=builder /app/build/client /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
