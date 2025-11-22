# Script PowerShell para executar o fix da tabela usuarios
# Este script conecta ao PostgreSQL e executa o SQL de correção

$dbHost = "localhost"
$dbPort = "5432"
$dbName = "Aprenda"
$dbUser = "postgres"
$dbPassword = "dudu0602"

$scriptPath = Join-Path $PSScriptRoot "fix-usuarios-id-sequence.sql"

Write-Host "=== Corrigindo tabela usuarios ===" -ForegroundColor Green
Write-Host "Conectando ao banco de dados: $dbName" -ForegroundColor Yellow

# Verificar se o psql está disponível
$psqlPath = Get-Command psql -ErrorAction SilentlyContinue

if (-not $psqlPath) {
    Write-Host "ERRO: psql não encontrado no PATH." -ForegroundColor Red
    Write-Host "Por favor, instale o PostgreSQL Client ou adicione o psql ao PATH." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Alternativa: Execute o script SQL manualmente no pgAdmin ou DBeaver:" -ForegroundColor Yellow
    Write-Host "  $scriptPath" -ForegroundColor Cyan
    exit 1
}

# Executar o script SQL
$env:PGPASSWORD = $dbPassword
$connectionString = "host=$dbHost port=$dbPort dbname=$dbName user=$dbUser"

try {
    & psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -f $scriptPath
    Write-Host ""
    Write-Host "Script executado com sucesso!" -ForegroundColor Green
    Write-Host "A tabela usuarios foi corrigida. Agora você pode criar usuários normalmente." -ForegroundColor Green
}
catch {
    Write-Host "ERRO ao executar o script: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Execute o script SQL manualmente no pgAdmin ou DBeaver:" -ForegroundColor Yellow
    Write-Host "  $scriptPath" -ForegroundColor Cyan
}
finally {
    Remove-Item Env:\PGPASSWORD -ErrorAction SilentlyContinue
}


