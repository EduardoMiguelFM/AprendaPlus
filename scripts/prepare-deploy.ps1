# Script PowerShell para preparar JAR para deploy no Release Pipeline
# Funciona quando o Release Pipeline roda em agente Windows

Write-Host "=========================================="
Write-Host "Preparando JAR para deploy"
Write-Host "=========================================="

$workDir = $env:SYSTEM_DEFAULTWORKINGDIRECTORY
$deployDir = Join-Path $workDir "drop"

# Criar diretório drop se não existir
New-Item -ItemType Directory -Path $deployDir -Force | Out-Null

Write-Host "Diretório de trabalho: $workDir"
Write-Host ""

# Procurar diretório do artefato
$artifactDir = Get-ChildItem -Path $workDir -Directory -Filter "*aprenda-plus-jar*" -ErrorAction SilentlyContinue | Select-Object -First 1

if (-not $artifactDir) {
    Write-Host "Procurando artefato em locais alternativos..."
    $jarFile = Get-ChildItem -Path $workDir -Recurse -Filter "Aprenda+-0.0.1-SNAPSHOT.jar" -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($jarFile) {
        $artifactDir = $jarFile.Directory
    }
}

if (-not $artifactDir -or -not (Test-Path $artifactDir.FullName)) {
    Write-Host "❌ ERRO: Diretório do artefato não encontrado!"
    Write-Host ""
    Write-Host "Estrutura de diretórios:"
    Get-ChildItem -Path $workDir -Directory -ErrorAction SilentlyContinue | Select-Object -First 20 | ForEach-Object { Write-Host "  $($_.FullName)" }
    exit 1
}

Write-Host "✅ Diretório do artefato encontrado: $($artifactDir.FullName)"
Write-Host ""
Write-Host "Conteúdo do artefato:"
Get-ChildItem -Path $artifactDir.FullName | Format-Table -AutoSize

# Procurar JAR (excluir plain JAR)
Write-Host ""
Write-Host "Procurando JAR executável..."
$jarFile = Get-ChildItem -Path $artifactDir.FullName -Filter "*.jar" -ErrorAction SilentlyContinue | 
Where-Object { $_.Name -notlike "*plain*" } | 
Select-Object -First 1

# Tentar caminhos específicos
if (-not $jarFile) {
    $possiblePaths = @(
        Join-Path $artifactDir.FullName "app.jar"
        Join-Path $artifactDir.FullName "Aprenda+-0.0.1-SNAPSHOT.jar"
    )
    
    foreach ($path in $possiblePaths) {
        if (Test-Path $path) {
            $jarFile = Get-Item $path
            Write-Host "✅ JAR encontrado: $path"
            break
        }
    }
}

# Procurar recursivamente
if (-not $jarFile) {
    $jarFile = Get-ChildItem -Path $artifactDir.FullName -Recurse -Filter "*.jar" -ErrorAction SilentlyContinue | 
    Where-Object { $_.Name -notlike "*plain*" } | 
    Select-Object -First 1
}

if (-not $jarFile -or -not (Test-Path $jarFile.FullName)) {
    Write-Host "❌ ERRO: JAR não encontrado!"
    Write-Host ""
    Write-Host "Todos os arquivos .jar encontrados:"
    Get-ChildItem -Path $artifactDir.FullName -Recurse -Filter "*.jar" -ErrorAction SilentlyContinue | 
    ForEach-Object { Write-Host "  $($_.FullName)" }
    exit 1
}

Write-Host ""
Write-Host "✅ JAR encontrado: $($jarFile.FullName)"
Write-Host "   Nome: $($jarFile.Name)"
Write-Host "   Tamanho: $([math]::Round($jarFile.Length / 1MB, 2)) MB"

# Copiar para drop/app.jar
Write-Host ""
Write-Host "Preparando JAR para deploy..."
Copy-Item -Path $jarFile.FullName -Destination (Join-Path $deployDir "app.jar") -Force
Write-Host "✅ JAR copiado para: $(Join-Path $deployDir 'app.jar')"

# Tentar criar ZIP se disponível
$zipAvailable = $false
try {
    $zipPath = Join-Path $deployDir "app.zip"
    Compress-Archive -Path (Join-Path $deployDir "app.jar") -DestinationPath $zipPath -Force
    Write-Host "✅ ZIP criado: $zipPath"
    $zipAvailable = $true
}
catch {
    Write-Host "⚠️  ZIP não disponível: $($_.Exception.Message)"
    Write-Host "   Usando JAR diretamente"
    $zipAvailable = $false
}

# Verificar
if (Test-Path (Join-Path $deployDir "app.jar")) {
    Write-Host "✅ Arquivos preparados com sucesso!"
    Write-Host ""
    Write-Host "Arquivos disponíveis:"
    Get-ChildItem -Path $deployDir | Format-Table -AutoSize
    Write-Host ""
    Write-Host "=========================================="
    Write-Host "Preparação concluída com sucesso!"
    Write-Host "=========================================="
    Write-Host ""
    if ($zipAvailable) {
        Write-Host "Use no Deploy Azure App Service:"
        Write-Host "  Package or folder: `$(System.DefaultWorkingDirectory)/drop/app.zip"
        Write-Host "  Startup command: java -jar /home/site/wwwroot/app.jar"
    }
    else {
        Write-Host "Use no Deploy Azure App Service:"
        Write-Host "  Package or folder: `$(System.DefaultWorkingDirectory)/drop/app.jar"
        Write-Host "  Startup command: java -jar /home/site/wwwroot/app.jar"
    }
}
else {
    Write-Host "❌ ERRO: Falha ao preparar arquivos!"
    exit 1
}
