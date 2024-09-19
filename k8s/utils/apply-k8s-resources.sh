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

echo "All images loaded into kind cluster."

# Apply all Persistent Volumes
kubectl apply -f persistent-volumes/.

# Apply all Persistent Volumes Claims
kubectl apply -f persistent-volume-claims/.

# Apply ConfigMap
kubectl apply -f configmaps/.

# Apply Secrets
kubectl apply -f secrets/.

# Apply all Deployments
kubectl apply -f deployments/databases/.
kubectl apply -f deployments/tools/.
kubectl apply -f deployments/.

# Apply all Services
kubectl apply -f services/databases/.
kubectl apply -f services/tools/.
kubectl apply -f services/.

echo "All Kubernetes resources applied successfully."