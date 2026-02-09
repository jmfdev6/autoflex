#!/bin/bash

# Script para gerar chaves JWT (RSA 2048 bits)
# Gera chave privada e pública para assinar e validar tokens JWT

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RESOURCES_DIR="$SCRIPT_DIR/../src/main/resources"

echo "Generating JWT keys..."

# Generate private key
openssl genrsa -out "$RESOURCES_DIR/privateKey.pem" 2048

# Generate public key from private key
openssl rsa -in "$RESOURCES_DIR/privateKey.pem" -pubout -out "$RESOURCES_DIR/publicKey.pem"

echo "JWT keys generated successfully!"
echo "Private key: $RESOURCES_DIR/privateKey.pem"
echo "Public key: $RESOURCES_DIR/publicKey.pem"
echo ""
echo "⚠️  IMPORTANT: Keep privateKey.pem secure and never commit it to version control!"
echo "   Add privateKey.pem to .gitignore"
