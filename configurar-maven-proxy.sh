#!/bin/bash

set -e

M2_DIR="$HOME/.m2"
SETTINGS_FILE="$M2_DIR/settings.xml"

# Tenta descobrir o proxy pelas variáveis de ambiente
PROXY_URL="${HTTPS_PROXY:-${https_proxy:-${HTTP_PROXY:-${http_proxy:-}}}}"

if [ -z "$PROXY_URL" ]; then
    echo "Erro: não foi possível descobrir o proxy automaticamente."
    echo "Defina HTTP_PROXY ou HTTPS_PROXY, por exemplo:"
    echo "  export HTTPS_PROXY=http://proxy.exemplo.edu.br:3128"
    exit 1
fi

# Remove protocolo
PROXY_URL="${PROXY_URL#http://}"
PROXY_URL="${PROXY_URL#https://}"

# Remove eventual usuário/senha
PROXY_URL="${PROXY_URL##*@}"

# Separa host e porta
PROXY_HOST="${PROXY_URL%%:*}"
PROXY_PORT="${PROXY_URL##*:}"

if [ "$PROXY_HOST" = "$PROXY_PORT" ] || [ -z "$PROXY_PORT" ]; then
    echo "Erro: não foi possível determinar a porta do proxy."
    echo "Proxy detectado: $PROXY_URL"
    exit 1
fi

PROXY_PORT=${PROXY_PORT%/}

mkdir -p "$M2_DIR"

cat > "$SETTINGS_FILE" <<EOF
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">

    <proxies>
        <proxy>
            <id>maven-proxy</id>
            <active>true</active>
            <protocol>http</protocol>
            <host>${PROXY_HOST}</host>
            <port>${PROXY_PORT}</port>
        </proxy>
    </proxies>

</settings>
EOF

echo "Proxy configurado com sucesso!"
echo "Host: $PROXY_HOST"
echo "Porta: $PROXY_PORT"
echo "Arquivo: $SETTINGS_FILE"

