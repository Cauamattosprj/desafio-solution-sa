# Frontend Dockerfile
# Stage 1: build static assets
FROM node:20-alpine AS builder
WORKDIR /app
COPY package.json pnpm-lock.yaml* ./
RUN npm install
COPY . .
ARG VITE_API_BASE=http://localhost:8080
ENV VITE_API_BASE=${VITE_API_BASE}
RUN npm run build

# Stage 2: serve with nginx
FROM nginx:1.25-alpine
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
