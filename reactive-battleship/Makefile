docker-machine-create:
	docker-machine create --virtualbox-cpu-count 4 --virtualbox-memory 5120 --virtualbox-disk-size 40960 reactive-battleship

build:
	cd reactive-position-generator; sbt clean compile assembly docker:publishLocal
	cd reactive-geofence-detector; sbt clean compile assembly docker:publishLocal
	cd reactive-websocket-client; sbt clean compile assembly docker:publishLocal
	cd kafka-consumer-lag-monitor; sbt clean compile assembly docker:publishLocal

start:
	docker-compose up -d zookeeper grafana; sleep 5;
	docker-compose up -d kafka1; sleep 10;
	docker-compose up -d geofence-detector websocket-client battleship-ui kafka-consumer-lag-monitor

fire:
	docker-compose up -d position-generator

stop:
	docker-compose kill && docker-compose rm -f
