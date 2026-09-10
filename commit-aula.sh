#!/bin/bash

# ============================================
# Script para commit, tag e push de uma aula
# Uso:
#   ./aula.sh "Seu Nome" "seu@email.com"
# ============================================

set -e

# Verifica os parâmetros
if [ "$#" -ne 2 ]; then
    echo "Uso: $0 \"Nome\" \"email\""
    echo "Exemplo: $0 \"João Silva\" \"joao@email.com\""
    exit 1
fi

GIT_NAME="$1"
GIT_EMAIL="$2"

# Configura nome e email do Git
echo "Configurando Git..."
git config user.name "$GIT_NAME"
git config user.email "$GIT_EMAIL"

echo "Git configurado:"
echo "  user.name:  $GIT_NAME"
echo "  user.email: $GIT_EMAIL"

# Solicita o token sem exibir na tela
echo
read -rsp "Cole o token do Git: " GIT_TOKEN
echo

if [ -z "$GIT_TOKEN" ]; then
    echo "Erro: o token não pode estar vazio."
    exit 1
fi

# Obtém mês e dia atuais
MES=$(date +%m)
DIA=$(date +%d)

NOME="aula-${MES}${DIA}"

echo
echo "Identificador da aula: $NOME"

# Verifica se existem alterações
if git diff --quiet && git diff --cached --quiet && [ -z "$(git ls-files --others --exclude-standard)" ]; then
    echo "Nenhuma alteração encontrada para commit."
    exit 1
fi

# Adiciona todos os arquivos ao stage
echo
echo "Adicionando arquivos ao stage..."
git add .

# Mostra o que será commitado
echo
echo "Arquivos que serão commitados:"
git status --short

# Commit
echo
echo "Criando commit: $NOME"
git commit -m "$NOME"

# Cria a tag
echo
echo "Criando tag: $NOME"
git tag "$NOME"

# Descobre a URL do remote
REMOTE_URL=$(git remote get-url origin)

if [ -z "$REMOTE_URL" ]; then
    echo "Erro: remote 'origin' não encontrado."
    exit 1
fi

# Faz o push usando o token apenas durante esta execução.
#
# Para HTTPS, transforma:
#   https://github.com/usuario/repositorio.git
#
# em:
#   https://TOKEN@github.com/usuario/repositorio.git
#
# O token não é armazenado permanentemente no git config.
if [[ "$REMOTE_URL" == https://* ]]; then

    PUSH_URL=$(echo "$REMOTE_URL" | sed "s#https://#https://${GIT_TOKEN}@#")

    echo
    echo "Enviando commits..."
    git push "$PUSH_URL"

    echo
    echo "Enviando tag..."
    git push "$PUSH_URL" "$NOME"

else
    echo "Erro: o remote 'origin' não está configurado usando HTTPS."
    echo "URL encontrada: $REMOTE_URL"
    exit 1
fi

# Limpa o token da variável
unset GIT_TOKEN
unset PUSH_URL

echo
echo "============================================"
echo "Concluído com sucesso!"
echo "Commit: $NOME"
echo "Tag:    $NOME"
echo "============================================"

