output "mysql_container_id" {
  description = "MySQL Container ID"
  value       = docker_container.mysql.id
}

output "mysql_container_name" {
  description = "MySQL Container Name"
  value       = docker_container.mysql.name
}

output "mysql_port" {
  description = "MySQL External Port"
  value       = var.mysql_external_port
}

output "app_container_id" {
  description = "Application Container ID"
  value       = docker_container.student_app.id
}

output "app_container_name" {
  description = "Application Container Name"
  value       = docker_container.student_app.name
}

output "app_url" {
  description = "Application URL"
  value       = "http://localhost:${var.app_external_port}/student"
}

output "swagger_url" {
  description = "Swagger UI URL"
  value       = "http://localhost:${var.app_external_port}/student/swagger-ui.html"
}

output "network_name" {
  description = "Docker Network Name"
  value       = docker_network.app_network.name
}

output "volume_name" {
  description = "MySQL Volume Name"
  value       = docker_volume.mysql_data.name
}