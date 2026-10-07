output "aws_region" {
  description = "AWS region"
  value       = var.aws_region
}

output "frontend_bucket_name" {
  description = "Frontend S3 bucket"
  value       = aws_s3_bucket.frontend.bucket
}

output "images_bucket_name" {
  description = "Images S3 bucket"
  value       = aws_s3_bucket.images.bucket
}

output "application_role_arn" {
  description = "IAM role used by the application"
  value       = aws_iam_role.application.arn
}

output "application_instance_profile_name" {
  description = "IAM instance profile used by Elastic Beanstalk"
  value       = aws_iam_instance_profile.application.name
}

output "vpc_id" {
  description = "Main application VPC ID"
  value       = aws_vpc.main.id
}

output "public_subnet_ids" {
  description = "Public subnet IDs"
  value = [
    aws_subnet.public.id
  ]
}

output "private_subnet_ids" {
  description = "Private subnet IDs for RDS"
  value = [
    aws_subnet.private.id,
    aws_subnet.private_b.id
  ]
}

output "application_security_group_id" {
  description = "Security group ID for the application"
  value       = aws_security_group.application.id
}

output "rds_security_group_id" {
  description = "Security group ID for RDS"
  value       = aws_security_group.rds.id
}

output "elastic_beanstalk_application_name" {
  description = "Elastic Beanstalk application name"
  value       = aws_elastic_beanstalk_application.backend.name
}

output "elastic_beanstalk_environment_name" {
  description = "Elastic Beanstalk environment name"
  value       = aws_elastic_beanstalk_environment.backend.name
}

output "elastic_beanstalk_environment_cname" {
  description = "Elastic Beanstalk environment CNAME"
  value       = aws_elastic_beanstalk_environment.backend.cname
}

output "rds_endpoint" {
  description = "RDS MySQL endpoint"
  value       = aws_db_instance.mysql.address
}

output "rds_port" {
  description = "RDS MySQL port"
  value       = aws_db_instance.mysql.port
}

output "rds_database_name" {
  description = "RDS database name"
  value       = aws_db_instance.mysql.db_name
}

output "rds_master_username" {
  description = "RDS master username"
  value       = aws_db_instance.mysql.username
}

output "rds_instance_identifier" {
  description = "RDS instance identifier"
  value       = aws_db_instance.mysql.identifier
}


