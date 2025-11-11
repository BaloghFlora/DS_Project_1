# --- STAGE 1: Build the React app ---
# Use a Node.js image to build the static files
FROM node:18-alpine AS build

WORKDIR /app

# Copy package.json and package-lock.json to leverage Docker cache
COPY package.json package-lock.json ./

# Install dependencies
RUN npm install --legacy-peer-deps

# Copy the rest of the application source code
COPY . .

# Build the production (static) files
RUN npm run build

# --- STAGE 2: Serve the app with Nginx ---
# Use a lightweight Nginx image
FROM nginx:1.25-alpine

# Copy the built static files from the 'build' stage
# into the Nginx public directory
COPY --from=build /app/build /usr/share/nginx/html

# We need a custom Nginx config for single-page apps (SPAs)
# Remove the default config
RUN rm /etc/nginx/conf.d/default.conf

# Copy our custom config file
COPY nginx.conf /etc/nginx/conf.d

# Expose port 80 (Nginx default)
EXPOSE 80

# Start Nginx in the foreground
CMD ["nginx", "-g", "daemon off;"]