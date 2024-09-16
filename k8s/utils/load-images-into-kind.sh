#!/bin/bash

# List of images to load
images=(
  "shaikrasheed99/api-gateway:latest"
  "shaikrasheed99/order-service:latest"
  "shaikrasheed99/inventory-service:latest"
  "shaikrasheed99/notification-service:latest"
)

# Cluster name
cluster_name="kind"

# Loop through each image and load it into kind
for image in "${images[@]}"
do
  echo "Loading image $image into kind cluster..."
  kind load docker-image $image --name $cluster_name
done
