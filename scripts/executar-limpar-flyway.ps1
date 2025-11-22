# Script PowerShell para limpar histórico do Flyway
# Remove a migração V2 falhada para permitir nova tentativa

$dbHost = "localhost"
$dbPort = "5432"
$dbName = "Aprenda"
$dbUser = "postgres"
$dbPassword = "dudu0602"

$scriptPath = Join-Path $PSScriptRoot "limpar-flyway-v2.sql"

Write-Host "=== Limpando histórico do Flyway ===" -ForegroundColor Green
Write-Host "Removendo registro da migração V2 falhada..." -ForegroundColor Yellow

# Verificar se o psql está disponível
$psqlPath = Get-Command psql -ErrorAction SilentlyContinue

if (-not $psqlPath) {
    Write-Host "ERRO: psql não encontrado no PATH." -ForegroundColor Red
    Write-Host "Execute o script SQL manualmente no pgAdmin ou DBeaver:" -ForegroundColor Yellow
    Write-Host "  $scriptPath" -ForegroundColor Cyan
    exit 1
}

# Executar o script SQL
$env:PGPASSWORD = $dbPassword

try {
    & psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -f $scriptPath
    Write-Host ""
    Write-Host "Histórico limpo com sucesso!" -ForegroundColor Green
    Write-Host "Agora você pode reiniciar a aplicação e a migração V2 será executada novamente." -ForegroundColor Green
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


