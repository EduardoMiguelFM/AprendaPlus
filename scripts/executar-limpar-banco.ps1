# Script PowerShell para limpar completamente o banco de dados
# ATENÇÃO: Este script remove TODAS as tabelas e dados!

$dbHost = "localhost"
$dbPort = "5432"
$dbName = "Aprenda"
$dbUser = "postgres"
$dbPassword = "dudu0602"

$scriptPath = Join-Path $PSScriptRoot "limpar-banco-completo.sql"

Write-Host "=== LIMPANDO BANCO DE DADOS COMPLETAMENTE ===" -ForegroundColor Red
Write-Host "ATENÇÃO: Todas as tabelas e dados serão removidos!" -ForegroundColor Yellow
Write-Host ""

$confirmation = Read-Host "Deseja continuar? (S/N)"

if ($confirmation -ne "S" -and $confirmation -ne "s") {
    Write-Host "Operação cancelada." -ForegroundColor Yellow
    exit 0
}

Write-Host "Removendo todas as tabelas e histórico do Flyway..." -ForegroundColor Yellow

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
    Write-Host "Banco de dados limpo com sucesso!" -ForegroundColor Green
    Write-Host "Agora você pode reiniciar a aplicação e as migrações serão executadas do zero." -ForegroundColor Green
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


