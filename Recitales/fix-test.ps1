# Script para corregir el archivo de test
$testFile = "src\test\java\Servicios\ServicioContratacionTest.java"

# Leer el contenido
$content = Get-Content $testFile -Raw

# Reemplazar todas las líneas problemáticas
$content = $content -replace 'repositorioArtistas = new RepositorioArtistas', 'RepositorioArtistas repositorioArtistas = new RepositorioArtistas'
$content = $content -replace 'servicioConsulta = new ServicioConsulta', 'ServicioConsulta servicioConsulta = crearServicioConsulta'
$content = $content -replace 'recital\.agregarCancion\((.*?)\);', ''
$content = $content -replace '(\s+)// Actualizar servicio consulta con nuevos datos[\s\S]*?repositorioBandas\s*\);', ''

# Guardar el archivo corregido
$content | Set-Content $testFile -NoNewline

Write-Host "Archivo corregido exitosamente"
