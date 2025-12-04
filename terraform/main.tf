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

# Network
resource "docker_network" "app_network" {
  name = "student-network-g4"
}

# Volume
resource "docker_volume" "mysql_data" {
  name = "mysql_data_g4"
}

# MySQL Container
resource "docker_container" "mysql" {
  name  = "student-mysql-g4-terraform"
  image = "mysql:8.0"

  env = [
    "MYSQL_ROOT_PASSWORD=rootpassword",
    "MYSQL_DATABASE=student_management",
    "MYSQL_USER=app_user",
    "MYSQL_PASSWORD=app_password"
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
}

# App Container
resource "docker_container" "student_app" {
  name  = "student-app-g4-terraform"
  image = "ghalia08/2mpawi-g4-student:latest"

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
}

output "app_url" {
  value = "http://localhost:8083/student"
}