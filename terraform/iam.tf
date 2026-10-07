# ============================================================
# IAM role for the Spring Boot application
#
# Elastic Beanstalk EC2 instances assume this role.
#
# The application does NOT receive AWS access keys.
# AWS SDK uses the EC2 instance role automatically.
# ============================================================

resource "aws_iam_role" "application" {
  name = "${local.name}-application-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Effect = "Allow"

        Principal = {
          Service = "ec2.amazonaws.com"
        }

        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = {
    Name = "${local.name}-application-role"
  }
}


# ============================================================
# S3 permissions
#
# The application can manage objects in the images bucket.
#
# It has NO access to the frontend bucket.
# ============================================================

resource "aws_iam_role_policy" "application_s3" {
  name = "${local.name}-s3"

  role = aws_iam_role.application.id

  policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Sid    = "ListImagesBucket"
        Effect = "Allow"

        Action = [
          "s3:ListBucket"
        ]

        Resource = aws_s3_bucket.images.arn
      },
      {
        Sid    = "ManageImages"
        Effect = "Allow"

        Action = [
          "s3:GetObject",
          "s3:PutObject",
          "s3:DeleteObject"
        ]

        Resource = "${aws_s3_bucket.images.arn}/*"
      }
    ]
  })
}


# ============================================================
# Elastic Beanstalk / EC2 instance profile
#
# EC2 receives the application IAM role through this profile.
# ============================================================

resource "aws_iam_instance_profile" "application" {
  name = "${local.name}-application-profile"

  role = aws_iam_role.application.name

  tags = {
    Name = "${local.name}-application-profile"
  }
}
