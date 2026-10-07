# ============================================================
# Elastic Beanstalk service role
#
# This role is used by the Elastic Beanstalk service itself.
# It is different from the EC2 application role.
# ============================================================

resource "aws_iam_role" "elastic_beanstalk_service" {
  name = "${local.name}-eb-service-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Effect = "Allow"

        Principal = {
          Service = "elasticbeanstalk.amazonaws.com"
        }

        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = {
    Name = "${local.name}-eb-service-role"
  }
}


# ============================================================
# Elastic Beanstalk service role policies
# ============================================================

resource "aws_iam_role_policy_attachment" "elastic_beanstalk_enhanced_health" {
  role = aws_iam_role.elastic_beanstalk_service.name

  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSElasticBeanstalkEnhancedHealth"
}

resource "aws_iam_role_policy_attachment" "elastic_beanstalk_managed_updates" {
  role = aws_iam_role.elastic_beanstalk_service.name

  policy_arn = "arn:aws:iam::aws:policy/AWSElasticBeanstalkManagedUpdatesCustomerRolePolicy"
}


# ============================================================
# Allow the EC2 application role to operate as an
# Elastic Beanstalk web-tier instance.
#
# Our existing application IAM role is also used by the
# application itself for S3 and Secrets Manager.
# ============================================================

resource "aws_iam_role_policy_attachment" "application_web_tier" {
  role = aws_iam_role.application.name

  policy_arn = "arn:aws:iam::aws:policy/AWSElasticBeanstalkWebTier"
}


# ============================================================
# Elastic Beanstalk application
# ============================================================

resource "aws_elastic_beanstalk_application" "backend" {
  name        = "${local.name}-backend"
  description = "Lab-Away Spring Boot backend"

  tags = {
    Name = "${local.name}-backend"
  }
}


# ============================================================
# Elastic Beanstalk environment
#
# LOW-COST CONFIGURATION:
#
# - Single EC2 instance
# - No load balancer
# - No Auto Scaling group with multiple instances
# - EC2 is placed in the public subnet
# - RDS remains private
#
# We can move to a load-balanced environment later.
# ============================================================

resource "aws_elastic_beanstalk_environment" "backend" {
  name                = "${local.name}-backend-env"
  application         = aws_elastic_beanstalk_application.backend.name
  solution_stack_name = "64bit Amazon Linux 2023 v4.8.0 running Corretto 21"

  # ----------------------------------------------------------
  # Environment type
  # ----------------------------------------------------------

  setting {
    namespace = "aws:elasticbeanstalk:environment"
    name      = "EnvironmentType"
    value     = "SingleInstance"
  }

  # ----------------------------------------------------------
  # EC2 instance configuration
  # ----------------------------------------------------------

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "IamInstanceProfile"
    value     = aws_iam_instance_profile.application.name
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "InstanceType"
    value     = var.eb_instance_type
  }

  # ----------------------------------------------------------
  # VPC configuration
  #
  # The application runs in the public subnet so it can
  # communicate with external services without requiring
  # a NAT Gateway.
  # ----------------------------------------------------------

  setting {
    namespace = "aws:ec2:vpc"
    name      = "VPCId"
    value     = aws_vpc.main.id
  }

  setting {
    namespace = "aws:ec2:vpc"
    name      = "Subnets"
    value     = aws_subnet.public.id
  }

  setting {
    namespace = "aws:ec2:vpc"
    name      = "AssociatePublicIpAddress"
    value     = "true"
  }

  # ----------------------------------------------------------
  # Security group
  # ----------------------------------------------------------

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "SecurityGroups"
    value     = aws_security_group.application.id
  }

  # ----------------------------------------------------------
  # Application port
  #
  # Spring Boot listens on port 5000.
  # Elastic Beanstalk's nginx proxy listens publicly and
  # forwards requests to the Spring Boot application.
  # ----------------------------------------------------------

  setting {
    namespace = "aws:elasticbeanstalk:environment:proxy"
    name      = "ProxyServer"
    value     = "nginx"
  }

  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "SERVER_PORT"
    value     = "5000"
  }

  # ----------------------------------------------------------
  # Application environment
  # ----------------------------------------------------------

  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "SPRING_PROFILES_ACTIVE"
    value     = "production"
  }

  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "AWS_REGION"
    value     = var.aws_region
  }

  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "AWS_S3_BUCKET_NAME"
    value     = aws_s3_bucket.images.bucket
  }

  # ----------------------------------------------------------
  # Health reporting
  # ----------------------------------------------------------

  setting {
    namespace = "aws:elasticbeanstalk:healthreporting:system"
    name      = "SystemType"
    value     = "enhanced"
  }

  # ----------------------------------------------------------
  # Managed platform updates
  # ----------------------------------------------------------

  setting {
    namespace = "aws:elasticbeanstalk:managedactions"
    name      = "ManagedActionsEnabled"
    value     = "true"
  }

  setting {
    namespace = "aws:elasticbeanstalk:managedactions"
    name      = "PreferredStartTime"
    value     = "Sun:03:00"
  }

  setting {
    namespace = "aws:elasticbeanstalk:managedactions:platformupdate"
    name      = "UpdateLevel"
    value     = "minor"
  }

  tags = {
    Name = "${local.name}-backend-env"
  }

  depends_on = [
    aws_iam_role_policy_attachment.elastic_beanstalk_enhanced_health,
    aws_iam_role_policy_attachment.application_web_tier
  ]
}