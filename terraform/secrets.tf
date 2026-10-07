# ============================================================
# Application secrets
#
# Secret values are intentionally NOT stored in Terraform.
#
# Terraform creates the Secrets Manager secret itself.
# The actual values will be supplied separately after the
# AWS account is created.
# ============================================================

resource "aws_secretsmanager_secret" "application" {
  name        = "${local.name}/backend"
  description = "Secrets for the Lab-Away Spring Boot backend"

  recovery_window_in_days = 7

  tags = {
    Name = "${local.name}-backend-secrets"
  }
}


# ============================================================
# Allow the Spring Boot application to read its secret.
#
# The application can read ONLY this specific secret.
# ============================================================

resource "aws_iam_role_policy" "application_secrets" {
  name = "${local.name}-secrets"

  role = aws_iam_role.application.id

  policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Sid    = "ReadApplicationSecret"
        Effect = "Allow"

        Action = [
          "secretsmanager:GetSecretValue"
        ]

        Resource = aws_secretsmanager_secret.application.arn
      }
    ]
  })
}


# ============================================================
# Output the secret ARN.
#
# The ARN is not sensitive.
# The secret VALUE is never output.
# ============================================================

output "application_secret_arn" {
  description = "ARN of the application Secrets Manager secret"
  value       = aws_secretsmanager_secret.application.arn
}
