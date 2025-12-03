variable "environment" {
  description = "Environnement de déploiement (dev, staging, prod)"
  type        = string
  default     = "dev"
  
  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "L'environnement doit être: dev, staging, ou prod."
  }
}

variable "project_name" {
  description = "Nom du projet"
  type        = string
  default     = "student-management"
}