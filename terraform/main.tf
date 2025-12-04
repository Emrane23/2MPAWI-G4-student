terraform {
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0"
    }
  }
}

provider "docker" {
  host = "npipe:////./pipe/docker_engine"
}

# MySQL Database Image
resource "docker_image" "mysql" {
  name = "mysql:8.0"
}

# MySQL Database Container
resource "docker_container" "mysql" {
  name  = "student-mysql-g4-terraform"
  image = docker_image.mysql.image_id

  env = [
    "MYSQL_ROOT_PASSWORD=rootpassword",
    "MYSQL_DATABASE=student_management",
    "MYSQL_USER=app_user",
    "MYSQL_PASSWORD=app_password",
    "MYSQL_ROOT_HOST=%"
  ]

  command = [
    "--bind-address=0.0.0.0",
    "--default-authentication-plugin=mysql_native_password"
  ]

  ports {
    internal = 3306
    external = 3307
  }

  volumes {
    volume_name    = docker_volume.mysql_data.name
    container_path = "/var/lib/mysql"
  }

  networks_advanced {
    name = docker_network.app_network.name
  }

  # Add healthcheck
  healthcheck {
    test     = ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-prootpassword"]
    interval = "10s"
    timeout  = "5s"
    retries  = 5
  }
}

# Spring Boot Application Image
resource "docker_image" "student_app" {
  name         = "ghalia08/2mpawi-g4-student:latest"
  keep_locally = false
}

# Spring Boot Application Container
resource "docker_container" "student_app" {
  name  = "student-app-g4-terraform"
  image = docker_image.student_app.image_id

  env = [
    "SPRING_DATASOURCE_URL=jdbc:mysql://student-mysql-g4-terraform:3306/student_management",
    "SPRING_DATASOURCE_USERNAME=app_user",
    "SPRING_DATASOURCE_PASSWORD=app_password",
    "SPRING_JPA_HIBERNATE_DDL_AUTO=update"
  ]

  ports {
    internal = 8089
    external = 8083
  }

  networks_advanced {
    name = docker_network.app_network.name
  }

  # IMPORTANT: Make sure MySQL is healthy before starting app
  depends_on = [docker_container.mysql]

  # Add restart policy
  restart = "on-failure"
  
  # Maximum 3 restart attempts
  max_retry_count = 3
}

# Docker Network
resource "docker_network" "app_network" {
  name = "student-network-g4"
}

# Docker Volume for MySQL Data Persistence
resource "docker_volume" "mysql_data" {
  name = "mysql_data_g4"
}