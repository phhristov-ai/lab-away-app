data "aws_caller_identity" "current" {}

resource "aws_iam_openid_connect_provider" "github" {
  url = "https://token.actions.githubusercontent.com"

  client_id_list = [
    "sts.amazonaws.com"
  ]
}

resource "aws_iam_role" "github_deploy" {
  name        = "lab-away-github-actions-deploy"
  description = "Deploys the Lab Away frontend and backend from GitHub Actions"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"

    Statement = [{
      Effect = "Allow"

      Principal = {
        Federated = aws_iam_openid_connect_provider.github.arn
      }

      Action = "sts:AssumeRoleWithWebIdentity"

      Condition = {
        StringEquals = {
          "token.actions.githubusercontent.com:aud" = "sts.amazonaws.com"
          "token.actions.githubusercontent.com:sub" = "repo:${var.github_repository}:ref:refs/heads/${var.github_branch}"
        }
      }
    }]
  })
}

resource "aws_iam_role_policy" "github_deploy" {
  name = "lab-away-github-deployment"
  role = aws_iam_role.github_deploy.id

  policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Sid    = "FrontendBucketList"
        Effect = "Allow"

        Action = [
          "s3:ListBucket",
          "s3:GetBucketLocation"
        ]

        Resource = "arn:aws:s3:::${var.frontend_bucket}"
      },
      {
        Sid    = "FrontendUpload"
        Effect = "Allow"

        Action = [
          "s3:PutObject",
          "s3:DeleteObject",
          "s3:AbortMultipartUpload",
          "s3:ListMultipartUploadParts"
        ]

        Resource = "arn:aws:s3:::${var.frontend_bucket}/*"
      },
      {
        Sid    = "ElasticBeanstalkArtifactBucket"
        Effect = "Allow"

        Action = [
          "s3:ListBucket",
          "s3:GetBucketLocation"
        ]

        Resource = "arn:aws:s3:::${var.beanstalk_bucket}"
      },
      {
        Sid    = "ElasticBeanstalkArtifactUpload"
        Effect = "Allow"

        Action = [
          "s3:PutObject",
          "s3:GetObject",
          "s3:AbortMultipartUpload"
        ]

        Resource = "arn:aws:s3:::${var.beanstalk_bucket}/*"
      },
      {
        Sid    = "ElasticBeanstalkDeployment"
        Effect = "Allow"

        Action = [
          "elasticbeanstalk:CreateApplicationVersion",
          "elasticbeanstalk:UpdateEnvironment",
          "elasticbeanstalk:DescribeApplications",
          "elasticbeanstalk:DescribeEnvironments",
          "elasticbeanstalk:DescribeEvents",
          "elasticbeanstalk:DescribeApplicationVersions",
          "elasticbeanstalk:ListAvailableSolutionStacks",
          "elasticbeanstalk:ListPlatformVersions",
          "elasticbeanstalk:CreateStorageLocation"
        ]

        Resource = "*"
      },
      {
        Sid    = "InvalidateFrontendCache"
        Effect = "Allow"

        Action = [
          "cloudfront:CreateInvalidation"
        ]

        Resource = "arn:aws:cloudfront::${data.aws_caller_identity.current.account_id}:distribution/${var.cloudfront_distribution_id}"
      }
    ]
  })
}

output "github_deploy_role_arn" {
  description = "Role ARN for GitHub Actions OIDC authentication"
  value       = aws_iam_role.github_deploy.arn
}