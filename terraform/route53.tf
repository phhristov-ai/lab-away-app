
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

# ============================================================
# Root domain -> CloudFront frontend
# ============================================================

resource "aws_route53_record" "frontend_root" {
  zone_id = aws_route53_zone.main.zone_id
  name    = var.domain_name
  type    = "A"

  alias {
    name                   = aws_cloudfront_distribution.frontend.domain_name
    zone_id                = aws_cloudfront_distribution.frontend.hosted_zone_id
    evaluate_target_health = false
  }
}

# ============================================================
# www subdomain -> CloudFront frontend
# ============================================================

resource "aws_route53_record" "frontend_www" {
  zone_id = aws_route53_zone.main.zone_id
  name    = "www.${var.domain_name}"
  type    = "A"

  alias {
    name                   = aws_cloudfront_distribution.frontend.domain_name
    zone_id                = aws_cloudfront_distribution.frontend.hosted_zone_id
    evaluate_target_health = false
  }
}
