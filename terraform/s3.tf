# ============================================================
# Frontend S3 bucket
#
# Private bucket.
# CloudFront will access this bucket using Origin Access
# Control (OAC).
#
# The bucket is NOT publicly accessible.
# ============================================================

resource "aws_s3_bucket" "frontend" {
  bucket = var.frontend_bucket_name

  tags = {
    Name = "${local.name}-frontend"
  }
}

resource "aws_s3_bucket_versioning" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_public_access_block" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}


# ============================================================
# Images S3 bucket
#
# Private bucket.
#
# The Spring Boot application accesses this bucket through
# its Elastic Beanstalk EC2 IAM role.
# ============================================================

resource "aws_s3_bucket" "images" {
  bucket = var.images_bucket_name

  tags = {
    Name = "${local.name}-images"
  }
}

resource "aws_s3_bucket_versioning" "images" {
  bucket = aws_s3_bucket.images.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_public_access_block" "images" {
  bucket = aws_s3_bucket.images.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "images" {
  bucket = aws_s3_bucket.images.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}


# Remove old frontend object versions after 30 days.
resource "aws_s3_bucket_lifecycle_configuration" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  rule {
    id     = "expire-old-frontend-versions"
    status = "Enabled"

    filter {}

    noncurrent_version_expiration {
      noncurrent_days = 30
    }

    # Remove expired delete markers when they are no longer needed.
    expiration {
      expired_object_delete_marker = true
    }
  }
}

# Remove old image versions after 90 days.
resource "aws_s3_bucket_lifecycle_configuration" "images" {
  bucket = aws_s3_bucket.images.id

  rule {
    id     = "expire-old-image-versions"
    status = "Enabled"

    filter {}

    noncurrent_version_expiration {
      noncurrent_days = 90
    }

    expiration {
      expired_object_delete_marker = true
    }


    abort_incomplete_multipart_upload {
      days_after_initiation = 7
    }
  }
}
