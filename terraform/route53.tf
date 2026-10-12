
# ============================================================
# Public DNS hosted zone for lab-away.com
# ============================================================

resource "aws_route53_zone" "main" {
  name = var.domain_name

  comment = "Public DNS zone for Lab-Away"

  tags = {
    Name = "${local.name}-public-zone"
  }
}

