#!/bin/bash

# Apply all Persistent Volumes
kubectl apply -f persistent-volumes/.

# Apply all Persistent Volumes Claims
kubectl apply -f persistent-volume-claims/.

# Apply all Deployments
kubectl apply -f deployments/databases/.
kubectl apply -f deployments/tools/.
kubectl apply -f deployments/.

# Apply all Services
kubectl apply -f services/databases/.
kubectl apply -f services/tools/.
kubectl apply -f services/.