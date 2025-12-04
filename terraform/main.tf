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

# Docker Network (create first)
resource "docker_network" "app_network" {
  name = "student-network-g4"
}

# Docker Volume for MySQL
resource "docker_volume" "mysql_data" {
  name = "mysql_data_g4"
}

# MySQL Database Image
resource "docker_image" "mysql" {
  name         = "mysql:8.0"
  keep_locally = true
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

  restart = "unless-stopped"
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

  depends_on = [docker_container.mysql]

  restart = "unless-stopped"
}

# Outputs
output "mysql_container_name" {
  description = "MySQL Container Name"
  value       = docker_container.mysql.name
}

output "app_container_name" {
  description = "Application Container Name"
  value       = docker_container.student_app.name
}

output "app_url" {
  description = "Application URL"
  value       = "http://localhost:8083/student"
}

output "swagger_url" {
  description = "Swagger UI URL"
  value       = "http://localhost:8083/student/swagger-ui.html"
}

output "network_name" {
  description = "Docker Network Name"
  value       = docker_network.app_network.name
}

output "volume_name" {
  description = "MySQL Volume Name"
  value       = docker_volume.mysql_data.name
}