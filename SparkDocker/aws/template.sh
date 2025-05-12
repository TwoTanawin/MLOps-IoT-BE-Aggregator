#!/bin/bash
# Update system and install Docker
apt-get update -y
apt-get install -y docker.io curl

# Enable Docker
systemctl start docker
systemctl enable docker

# Install Docker Compose v2
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# Create working directory
mkdir -p /opt/mlops_spark && cd /opt/mlops_spark

# Write docker-compose.yml with restart policy
cat <<EOF > docker-compose.yml
services:
  spark-master:
    image: bitnami/spark:3.2.1
    container_name: mlops_spark_master
    restart: always
    environment:
      JDK_JAVA_OPTIONS: --add-opens=java.base/sun.nio.ch=ALL-UNNAMED
      SPARK_MODE: master
    ports:
      - "8080:8080"
    volumes:
      - "./spark-logs:/spark/logs"

  spark-worker-1:
    image: bitnami/spark:3.2.1
    container_name: mlops_spark_worker_1
    restart: always
    depends_on:
      - spark-master
    environment:
      JDK_JAVA_OPTIONS: --add-opens=java.base/sun.nio.ch=ALL-UNNAMED
      SPARK_MODE: worker
      SPARK_MASTER_URL: spark://spark-master:7077
    volumes:
      - "./spark-logs:/spark/logs"

  spark-worker-2:
    image: bitnami/spark:3.2.1
    container_name: mlops_spark_worker_2
    restart: always
    depends_on:
      - spark-master
    environment:
      JDK_JAVA_OPTIONS: --add-opens=java.base/sun.nio.ch=ALL-UNNAMED
      SPARK_MODE: worker
      SPARK_MASTER_URL: spark://spark-master:7077
    volumes:
      - "./spark-logs:/spark/logs"
EOF

# Create systemd service to run Docker Compose on boot
cat <<EOF > /etc/systemd/system/mlops_spark.service
[Unit]
Description=MLOps Spark Docker Compose Service
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=true
WorkingDirectory=/opt/mlops_spark
ExecStartPre=/usr/local/bin/docker-compose pull
ExecStart=/usr/local/bin/docker-compose up -d
ExecStop=/usr/local/bin/docker-compose down
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
EOF

# Reload systemd and enable our service
systemctl daemon-reload
systemctl enable mlops_spark
systemctl start mlops_spark

# Optional: prevent instance from going idle (EC2 doesn't sleep, but just in case)
systemctl mask sleep.target suspend.target hibernate.target hybrid-sleep.target