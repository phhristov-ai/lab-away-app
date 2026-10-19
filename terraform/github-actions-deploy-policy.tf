resource "aws_iam_role_policy" "github_actions_deploy" {
  name = "${local.name}-github-actions-deploy"
  role = aws_iam_role.github_actions_deploy.id

  policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Sid    = "FrontendBucketList"
        Effect = "Allow"
        Action = [
          "s3:ListBucket"
        ]
        Resource = [
          aws_s3_bucket.frontend.arn
        ]
      },
      {
        Sid    = "FrontendObjectDeployment"
        Effect = "Allow"
        Action = [
          "s3:GetObject",
          "s3:PutObject"
        ]
        Resource = [
          "${aws_s3_bucket.frontend.arn}/*"
        ]
      },
      {
        Sid    = "BackendDeploymentBundleUpload"
        Effect = "Allow"
        Action = [
          "s3:GetObject",
          "s3:PutObject",
          "s3:AbortMultipartUpload",
          "s3:ListMultipartUploadParts"
        ]
        Resource = [
          "arn:aws:s3:::elasticbeanstalk-eu-north-1-447839931482/lab-away-deployments/*"
        ]
      },
      {
        Sid      = "ElasticBeanstalkStorageBucket"
        Effect   = "Allow"
        Action   = ["s3:CreateBucket"]
        Resource = [
          "arn:aws:s3:::elasticbeanstalk-eu-north-1-447839931482"
        ]
      },
      {
        Sid    = "CloudFrontInvalidation"
        Effect = "Allow"
        Action = [
          "cloudfront:CreateInvalidation"
        ]
        Resource = [
          aws_cloudfront_distribution.frontend.arn
        ]
      },
      {
        Sid    = "ElasticBeanstalkDeploy"
        Effect = "Allow"
        Action = [
          "elasticbeanstalk:CreateApplicationVersion",
          "elasticbeanstalk:UpdateEnvironment"
        ]
        Resource = [
          "arn:aws:elasticbeanstalk:${var.aws_region}:447839931482:application/${aws_elastic_beanstalk_application.backend.name}",
          "arn:aws:elasticbeanstalk:${var.aws_region}:447839931482:applicationversion/${aws_elastic_beanstalk_application.backend.name}/*",
          "arn:aws:elasticbeanstalk:${var.aws_region}:447839931482:environment/${aws_elastic_beanstalk_application.backend.name}/${aws_elastic_beanstalk_environment.backend.name}"
        ]
      },
      {
        Sid    = "ElasticBeanstalkReadDeploymentStatus"
        Effect = "Allow"
        Action = [
          "elasticbeanstalk:DescribeApplications",
          "elasticbeanstalk:DescribeApplicationVersions",
          "elasticbeanstalk:DescribeEnvironments",
          "elasticbeanstalk:DescribeEvents"
        ]
        Resource = "*"
      }
    ]
  })
}
