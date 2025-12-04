terraform {
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0.1"
    }
  }
}

provider "docker" {}

# Network
resource "docker_network" "student_network" {
  name = "student-network-g4"
}

# Volume for MySQL
resource "docker_volume" "mysql_data" {
  name = "mysql_data_g4"
}

# Volume for Prometheus data
resource "docker_volume" "prometheus_data" {
  name = "prometheus_data_g4"
}

# Volume for Grafana data
resource "docker_volume" "grafana_data" {
  name = "grafana_data_g4"
}

# MySQL Container
resource "docker_container" "mysql" {
  name  = "student-mysql-g4-terraform"
  image = "mysql:8.0"

  networks_advanced {
    name = docker_network.student_network.name
  }

  env = [
    "MYSQL_ROOT_PASSWORD=root",
    "MYSQL_DATABASE=studentdb"
  ]

  ports {
    internal = 3306
    external = 3307
  }

  volumes {
    volume_name    = docker_volume.mysql_data.name
    container_path = "/var/lib/mysql"
  }

  healthcheck {
    test     = ["CMD", "mysqladmin", "ping", "-h", "localhost"]
    interval = "10s"
    timeout  = "5s"
    retries  = 5
  }
}

# Spring Boot Application Container
resource "docker_container" "student_app" {
  name  = "student-app-g4-terraform"
  image = "ghalia08/2mpawi-g4-student:latest"

  networks_advanced {
    name = docker_network.student_network.name
  }

  env = [
    "SPRING_DATASOURCE_URL=jdbc:mysql://student-mysql-g4-terraform:3306/studentdb",
    "SPRING_DATASOURCE_USERNAME=root",
    "SPRING_DATASOURCE_PASSWORD=root"
  ]

  ports {
    internal = 8083
    external = 8083
  }

  depends_on = [docker_container.mysql]

  restart = "unless-stopped"
}

# Prometheus Container
resource "docker_container" "prometheus" {
  name  = "prometheus-g4-terraform"
  image = "prom/prometheus:latest"

  networks_advanced {
    name = docker_network.student_network.name
  }

  ports {
    internal = 9090
    external = 9090
  }

  volumes {
    volume_name    = docker_volume.prometheus_data.name
    container_path = "/prometheus"
  }

  # Mount prometheus config
  volumes {
    host_path      = abspath("${path.module}/prometheus.yml")
    container_path = "/etc/prometheus/prometheus.yml"
    read_only      = true
  }

  command = [
    "--config.file=/etc/prometheus/prometheus.yml",
    "--storage.tsdb.path=/prometheus",
    "--web.console.libraries=/usr/share/prometheus/console_libraries",
    "--web.console.templates=/usr/share/prometheus/consoles"
  ]

  restart = "unless-stopped"

  depends_on = [docker_container.student_app]
}

# Grafana Container
resource "docker_container" "grafana" {
  name  = "grafana-g4-terraform"
  image = "grafana/grafana:latest"

  networks_advanced {
    name = docker_network.student_network.name
  }

  env = [
    "GF_SECURITY_ADMIN_USER=admin",
    "GF_SECURITY_ADMIN_PASSWORD=admin123",
    "GF_USERS_ALLOW_SIGN_UP=false"
  ]

  ports {
    internal = 3000
    external = 3000
  }

  volumes {
    volume_name    = docker_volume.grafana_data.name
    container_path = "/var/lib/grafana"
  }

  restart = "unless-stopped"

  depends_on = [docker_container.prometheus]
}

# Outputs
output "application_url" {
  value = "http://localhost:8083/student"
}

output "prometheus_url" {
  value = "http://localhost:9090"
}

output "grafana_url" {
  value = "http://localhost:3000 (admin/admin123)"
}