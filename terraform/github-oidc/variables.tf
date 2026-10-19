variable "github_repository" {
  type    = string
  default = "phhristov-ai/lab-away-app"
}

variable "github_branch" {
  type    = string
  default = "main"
}

variable "frontend_bucket" {
  type    = string
  default = "lab-away-production-frontend-20261007"
}

variable "beanstalk_bucket" {
  type    = string
  default = "elasticbeanstalk-eu-north-1-029123881976"
}

variable "cloudfront_distribution_id" {
  description = "ID of the existing frontend CloudFront distribution"
  type        = string
}