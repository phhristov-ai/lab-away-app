# ============================================================
# Elastic Beanstalk / application security group
#
# This security group will be attached to the EC2 instances
# running the Spring Boot application.
#
# The application listens on port 5000.
# ============================================================

resource "aws_security_group" "application" {
  name        = "${local.name}-application-sg"
  description = "Security group for the Lab-Away Spring Boot application"
  vpc_id      = aws_vpc.main.id

  tags = {
    Name = "${local.name}-application-sg"
  }
}


# ============================================================
# Application ingress
#
# HTTP/HTTPS traffic will eventually be handled by the
# Elastic Beanstalk load balancer.
#
# For now, allow HTTP and HTTPS so the load balancer can
# communicate with the application infrastructure.
# ============================================================

resource "aws_vpc_security_group_ingress_rule" "application_http" {
  security_group_id = aws_security_group.application.id

  cidr_ipv4   = "0.0.0.0/0"
  from_port   = 80
  to_port     = 80
  ip_protocol = "tcp"

  description = "HTTP traffic"
}

resource "aws_vpc_security_group_ingress_rule" "application_https" {
  security_group_id = aws_security_group.application.id

  cidr_ipv4   = "0.0.0.0/0"
  from_port   = 443
  to_port     = 443
  ip_protocol = "tcp"

  description = "HTTPS traffic"
}


# ============================================================
# Application port
#
# Spring Boot listens on port 5000.
#
# We allow traffic from within the VPC rather than exposing
# port 5000 directly to the internet.
#
# The Elastic Beanstalk load balancer can therefore forward
# traffic to the application on port 5000.
# ============================================================

resource "aws_vpc_security_group_ingress_rule" "application_5000" {
  security_group_id = aws_security_group.application.id

  cidr_ipv4   = aws_vpc.main.cidr_block
  from_port   = 5000
  to_port     = 5000
  ip_protocol = "tcp"

  description = "Spring Boot traffic from within the VPC"
}


# ============================================================
# Application egress
#
# The application needs outbound access for things such as:
#
# - RDS
# - S3
# - Secrets Manager
# - SMTP
# - Stripe
# - PayPal
# - Google Analytics
#
# We will tighten this later if there is a reason to do so.
# ============================================================

resource "aws_vpc_security_group_egress_rule" "application_all" {
  security_group_id = aws_security_group.application.id

  cidr_ipv4   = "0.0.0.0/0"
  ip_protocol = "-1"

  description = "Allow outbound application traffic"
}


# ============================================================
# RDS security group
#
# This security group protects the MySQL database.
#
# There is intentionally NO internet ingress rule here.
# ============================================================

resource "aws_security_group" "rds" {
  name        = "${local.name}-rds-sg"
  description = "Security group for the Lab-Away RDS MySQL database"
  vpc_id      = aws_vpc.main.id

  tags = {
    Name = "${local.name}-rds-sg"
  }
}


# ============================================================
# RDS MySQL ingress
#
# Only the application security group can connect to MySQL.
#
# No 0.0.0.0/0 rule.
# No public database access.
# ============================================================

resource "aws_vpc_security_group_ingress_rule" "rds_mysql" {
  security_group_id = aws_security_group.rds.id

  referenced_security_group_id = aws_security_group.application.id

  from_port   = 3306
  to_port     = 3306
  ip_protocol = "tcp"

  description = "MySQL access from the Spring Boot application"
}


# ============================================================
# RDS egress
#
# RDS normally needs very little outbound access. We keep the
# default unrestricted egress for simplicity initially.
# ============================================================

resource "aws_vpc_security_group_egress_rule" "rds_all" {
  security_group_id = aws_security_group.rds.id

  cidr_ipv4   = "0.0.0.0/0"
  ip_protocol = "-1"

  description = "Allow outbound RDS traffic"
}
