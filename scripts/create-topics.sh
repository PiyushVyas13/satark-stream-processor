echo "Waiting for kafka to come online..."

cub kafka-ready -b kafka:9092 20 1

kafka-topics \
  --bootstrap-server kafka:9092 \
  --topic reports \
  --replication-factor 1 \
  --partitions 4 \
  --create

kafka-topics \
  --bootstrap-server kafka:9092 \
  --topic mass-reports \
  --replication-factor 1 \
  --partitions 4 \
  --create

sleep infinity