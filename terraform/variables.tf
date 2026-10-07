variable "aws_region" {
  description = "AWS region for the application infrastructure"
  type        = string
  default     = "eu-central-1"
}

variable "project_name" {
  description = "Project name"
  type        = string
  default     = "lab-away"
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "production"
}

variable "domain_name" {
  description = "Primary application domain"
  type        = string
  default     = "lab-away.com"
}

variable "frontend_bucket_name" {
  description = "S3 bucket for the React frontend"
  type        = string
}

variable "images_bucket_name" {
  description = "S3 bucket for uploaded images"
  type        = string
}

variable "eb_instance_type" {
  description = "EC2 instance type used by Elastic Beanstalk"
  type        = string
  default     = "t3.micro"
}

variable "rds_instance_class" {
  description = "RDS instance class"
  type        = string
  default     = "db.t4g.micro"
}

variable "rds_allocated_storage" {
  description = "Initial RDS storage in GB"
  type        = number
  default     = 20
}

variable "rds_max_allocated_storage" {
  description = "Maximum RDS storage in GB for autoscaling"
  type        = number
  default     = 50
}

variable "rds_database_name" {
  description = "Initial application database name"
  type        = string
  default     = "labaway"
}

variable "rds_master_username" {
  description = "RDS master username"
  type        = string
  default     = "labaway_admin"
}

variable "rds_backup_retention_days" {
  description = "Number of days to retain automated RDS backups"
  type        = number
  default     = 7
}

