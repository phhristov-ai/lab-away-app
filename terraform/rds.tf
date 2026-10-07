# ============================================================
# RDS MySQL
#
# Initial low-cost configuration:
# - Single-AZ
# - Private subnets only
# - No public accessibility
# - Automated backups enabled
# - Encryption enabled
# - Storage autoscaling enabled
# ============================================================

resource "aws_db_subnet_group" "mysql" {
  name = "${local.name}-mysql"

  subnet_ids = [
    aws_subnet.private.id,
    aws_subnet.private_b.id
  ]

  description = "Private subnet group for Lab-Away MySQL"

  tags = {
    Name = "${local.name}-mysql-subnet-group"
  }
}


# ============================================================
# RDS parameter group
#
# MySQL 8.4 is the current long-term choice we want to use.
# ============================================================

resource "aws_db_parameter_group" "mysql" {
  name   = "${local.name}-mysql84"
  family = "mysql8.4"

  tags = {
    Name = "${local.name}-mysql84"
  }
}


# ============================================================
# RDS instance
# ============================================================

resource "aws_db_instance" "mysql" {
  identifier = "${local.name}-mysql"

  engine         = "mysql"
  engine_version = "8.4"

  instance_class = var.rds_instance_class

  allocated_storage     = var.rds_allocated_storage
  max_allocated_storage = var.rds_max_allocated_storage
  storage_type          = "gp3"
  storage_encrypted     = true

  db_name  = var.rds_database_name
  username = var.rds_master_username

  # Password is generated separately and stored in Terraform
  # state as a sensitive value. We will improve this later
  # if we want the database password managed entirely by AWS.
  password = random_password.rds.result

  port = 3306

  db_subnet_group_name   = aws_db_subnet_group.mysql.name
  vpc_security_group_ids = [aws_security_group.rds.id]
  parameter_group_name   = aws_db_parameter_group.mysql.name

  publicly_accessible = false

  multi_az = false

  backup_retention_period = var.rds_backup_retention_days
  backup_window           = "03:00-04:00"

  maintenance_window = "sun:04:00-sun:05:00"

  auto_minor_version_upgrade = true

  deletion_protection = false
  skip_final_snapshot = true

  copy_tags_to_snapshot = true

  performance_insights_enabled = false

  monitoring_interval = 0

  tags = {
    Name = "${local.name}-mysql"
  }
}


# ============================================================
# Database password
#
# Terraform generates a strong random password.
# The value is marked sensitive and is NOT exposed as an output.
# ============================================================

resource "random_password" "rds" {
  length  = 32
  special = true
}
