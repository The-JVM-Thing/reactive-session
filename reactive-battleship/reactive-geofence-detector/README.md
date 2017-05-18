# streaming-position-generator
Forked from [https://github.com/foyst/bulk-position-generator]()

Generates an infinite stream coordinates within a specified "box", and publishes these to a Kafka topic

How to build Docker Image:
sbt clean compile assembly docker:publishLocal