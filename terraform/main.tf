terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "us-west-2"
}

resource "aws_s3_bucket" "student_app_bucket" {
  bucket_prefix = "student-management-app-"
  
  tags = {
    Name        = "Student Management App"
    Environment = "dev"
    ManagedBy   = "Terraform"
    Project     = "DevOps Pipeline"
  }
}

output "bucket_name" {
  value = aws_s3_bucket.student_app_bucket.bucket
}