variable "mysql_root_password" {
  description = "MySQL root password"
  type        = string
  default     = "rootpassword"
  sensitive   = true
}

variable "mysql_database" {
  description = "MySQL database name"
  type        = string
  default     = "student_management"
}

variable "mysql_user" {
  description = "MySQL application user"
  type        = string
  default     = "app_user"
}

variable "mysql_password" {
  description = "MySQL application user password"
  type        = string
  default     = "app_password"
  sensitive   = true
}

variable "app_external_port" {
  description = "Application external port"
  type        = number
  default     = 8083
}

variable "mysql_external_port" {
  description = "MySQL external port"
  type        = number
  default     = 3307
}

variable "docker_image" {
  description = "Docker image name"
  type        = string
  default     = "ghalia08/2mpawi-g4-student:latest"
}