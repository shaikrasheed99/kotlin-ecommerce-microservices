cd discovery-server
./gradlew bootrun &
cd ..

cd api-gateway
./gradlew bootrun &
cd ..

cd product-service
./gradlew bootrun &
cd ..

cd inventory-service
./gradlew bootrun &
cd ..

cd notification-service
./gradlew bootrun &
cd ..

cd order-service
./gradlew bootrun &
cd ..