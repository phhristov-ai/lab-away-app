
# ============================================================
# Amazon SES domain identity
# ============================================================

resource "aws_sesv2_email_identity" "domain" {
  email_identity = var.domain_name

  tags = {
    Name = "${local.name}-ses-domain"
  }
}

# ============================================================
# Allow the application to send email using this domain
# ============================================================

resource "aws_iam_role_policy" "application_ses" {
  name = "${local.name}-ses-send"
  role = aws_iam_role.application.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid      = "SendFromLabAwayDomain"
        Effect   = "Allow"
        Action   = ["ses:SendEmail"]
        Resource = aws_sesv2_email_identity.domain.arn

        Condition = {
          StringEquals = {
            "ses:FromAddress" = "sales@lab-away.com"
          }
        }
      }
    ]
  })
}

