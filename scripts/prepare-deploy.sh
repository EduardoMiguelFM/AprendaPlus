#!/bin/bash
set -e

echo "=========================================="
echo "Preparando JAR para deploy"
echo "=========================================="

WORK_DIR="$(System.DefaultWorkingDirectory)"
DEPLOY_DIR="$WORK_DIR/drop"
mkdir -p "$DEPLOY_DIR"

echo "Diretório de trabalho: $WORK_DIR"
echo ""

# Procurar o diretório do artefato (pode ter nome variável como "_Aprenda (29)")
ARTIFACT_DIR=$(find "$WORK_DIR" -type d -name "*aprenda-plus-jar" 2>/dev/null | head -n 1)

if [ -z "$ARTIFACT_DIR" ]; then
    echo "Procurando artefato em locais alternativos..."
    # Tentar encontrar qualquer diretório que contenha JARs
    ARTIFACT_DIR=$(find "$WORK_DIR" -type f -name "Aprenda+-0.0.1-SNAPSHOT.jar" 2>/dev/null | head -n 1 | xargs dirname 2>/dev/null)
fi

if [ -z "$ARTIFACT_DIR" ] || [ ! -d "$ARTIFACT_DIR" ]; then
    echo "❌ ERRO: Diretório do artefato não encontrado!"
    echo ""
    echo "Estrutura de diretórios:"
    find "$WORK_DIR" -maxdepth 2 -type d 2>/dev/null | head -20 || true
    exit 1
fi

echo "✅ Diretório do artefato encontrado: $ARTIFACT_DIR"
echo ""
echo "Conteúdo do artefato:"
ls -lah "$ARTIFACT_DIR" || true

# Procurar o JAR (excluir plain JAR)
echo ""
echo "Procurando JAR executável..."
JAR_FILE=$(find "$ARTIFACT_DIR" -type f -name "*.jar" -not -name "*plain*" 2>/dev/null | head -n 1)

# Tentar caminhos específicos se não encontrou
if [ -z "$JAR_FILE" ] || [ ! -f "$JAR_FILE" ]; then
    echo "Tentando caminhos específicos..."
    POSSIBLE_PATHS=(
        "$ARTIFACT_DIR/app.jar"
        "$ARTIFACT_DIR/drop/app.jar"
        "$ARTIFACT_DIR/Aprenda+-0.0.1-SNAPSHOT.jar"
        "$ARTIFACT_DIR/build/libs/Aprenda+-0.0.1-SNAPSHOT.jar"
    )
    
    for path in "${POSSIBLE_PATHS[@]}"; do
        if [ -f "$path" ]; then
            JAR_FILE="$path"
            echo "✅ JAR encontrado: $path"
            break
        fi
    done
fi

# Se ainda não encontrou, procurar recursivamente
if [ -z "$JAR_FILE" ] || [ ! -f "$JAR_FILE" ]; then
    echo "Procurando recursivamente em $ARTIFACT_DIR..."
    JAR_FILE=$(find "$ARTIFACT_DIR" -type f -name "*.jar" -not -name "*plain*" 2>/dev/null | head -n 1)
fi

# Se ainda não encontrou, listar tudo para debug
if [ -z "$JAR_FILE" ] || [ ! -f "$JAR_FILE" ]; then
    echo "❌ ERRO: JAR não encontrado!"
    echo ""
    echo "Todos os arquivos .jar encontrados:"
    find "$ARTIFACT_DIR" -type f -name "*.jar" 2>/dev/null || true
    exit 1
fi

echo ""
echo "✅ JAR encontrado: $JAR_FILE"
echo "   Nome: $(basename "$JAR_FILE")"
echo "   Tamanho: $(du -h "$JAR_FILE" | cut -f1)"
ls -lah "$JAR_FILE"

# Copiar para drop/app.jar
echo ""
echo "Preparando JAR para deploy..."

# Copiar para drop/app.jar (renomear para facilitar)
cp "$JAR_FILE" "$DEPLOY_DIR/app.jar"
echo "✅ JAR copiado para: $DEPLOY_DIR/app.jar"

# Tentar criar ZIP se disponível (opcional, mas recomendado)
cd "$DEPLOY_DIR"
if command -v zip >/dev/null 2>&1; then
    zip -q app.zip app.jar
    echo "✅ ZIP criado: $DEPLOY_DIR/app.zip"
    ZIP_AVAILABLE=true
else
    echo "⚠️  ZIP não disponível (comando 'zip' não encontrado)"
    echo "   Usando JAR diretamente"
    ZIP_AVAILABLE=false
fi

# Verificar
if [ -f "$DEPLOY_DIR/app.jar" ]; then
    echo "✅ Arquivos preparados com sucesso!"
    echo ""
    echo "Arquivos disponíveis:"
    ls -lah "$DEPLOY_DIR/"
    echo ""
    echo "=========================================="
    echo "Preparação concluída com sucesso!"
    echo "=========================================="
    echo ""
    if [ "$ZIP_AVAILABLE" = true ]; then
        echo "Use no Deploy Azure App Service:"
        echo "  Package or folder: \$(System.DefaultWorkingDirectory)/drop/app.zip"
        echo "  Startup command: java -jar /home/site/wwwroot/app.jar"
    else
        echo "Use no Deploy Azure App Service:"
        echo "  Package or folder: \$(System.DefaultWorkingDirectory)/drop/app.jar"
        echo "  Startup command: java -jar /home/site/wwwroot/app.jar"
    fi
else
    echo "❌ ERRO: Falha ao preparar arquivos!"
    exit 1
fi

