# ============================================================
# VPC
#
# Low-cost application network:
#
#   VPC
#   ├── Public subnet (AZ A)
#   │     └── Elastic Beanstalk / public-facing resources
#   │
#   ├── Private subnet (AZ A)
#   │     └── RDS
#   │
#   └── Private subnet (AZ B)
#         └── RDS subnet group
#
# No NAT Gateway is used to keep costs low.
# ============================================================

data "aws_availability_zones" "available" {
  state = "available"
}


# ============================================================
# VPC
# ============================================================

resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"

  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "${local.name}-vpc"
  }
}


# ============================================================
# Internet Gateway
#
# Used by the public subnet.
# ============================================================

resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${local.name}-igw"
  }
}


# ============================================================
# Public subnet
#
# Currently we use only ONE public subnet to keep the
# infrastructure simple and inexpensive.
# ============================================================

resource "aws_subnet" "public" {
  vpc_id = aws_vpc.main.id

  cidr_block = "10.0.1.0/24"

  availability_zone = data.aws_availability_zones.available.names[0]

  map_public_ip_on_launch = true

  tags = {
    Name = "${local.name}-public"
  }
}


# ============================================================
# Private subnet A
# ============================================================

resource "aws_subnet" "private" {
  vpc_id = aws_vpc.main.id

  cidr_block = "10.0.11.0/24"

  availability_zone = data.aws_availability_zones.available.names[0]

  map_public_ip_on_launch = false

  tags = {
    Name = "${local.name}-private-a"
  }
}


# ============================================================
# Private subnet B
#
# RDS requires a DB subnet group to contain subnets in
# at least two Availability Zones.
#
# This does NOT create a second RDS instance.
# ============================================================

resource "aws_subnet" "private_b" {
  vpc_id = aws_vpc.main.id

  cidr_block = "10.0.12.0/24"

  availability_zone = data.aws_availability_zones.available.names[1]

  map_public_ip_on_launch = false

  tags = {
    Name = "${local.name}-private-b"
  }
}


# ============================================================
# Public route table
#
# Internet-bound traffic goes through the Internet Gateway.
# ============================================================

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = {
    Name = "${local.name}-public-rt"
  }
}


# ============================================================
# Private route table
#
# No NAT Gateway is configured.
#
# This keeps the VPC inexpensive. Resources in the private
# subnets cannot directly access the public internet.
# ============================================================

resource "aws_route_table" "private" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${local.name}-private-rt"
  }
}


# ============================================================
# Public subnet -> public route table
# ============================================================

resource "aws_route_table_association" "public" {
  subnet_id = aws_subnet.public.id

  route_table_id = aws_route_table.public.id
}


# ============================================================
# Private subnet A -> private route table
# ============================================================

resource "aws_route_table_association" "private" {
  subnet_id = aws_subnet.private.id

  route_table_id = aws_route_table.private.id
}


# ============================================================
# Private subnet B -> private route table
# ============================================================

resource "aws_route_table_association" "private_b" {
  subnet_id = aws_subnet.private_b.id

  route_table_id = aws_route_table.private.id
}