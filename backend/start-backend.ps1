$ErrorActionPreference = 'Stop'

$envFile = Join-Path $PSScriptRoot '..\.env'
if (Test-Path $envFile) {
    $envValues = @{}
    Get-Content $envFile | ForEach-Object {
        if ($_ -match '^\s*([^#=\s]+)\s*=\s*(.*?)\s*$') {
            $envValues[$matches[1]] = $matches[2]
        }
    }

    $useMySql = $envValues['SPRING_PROFILES_ACTIVE'] -eq 'mysql'
    foreach ($name in $envValues.Keys) {
        if ($useMySql -or -not $name.StartsWith('DATABASE_')) {
            [Environment]::SetEnvironmentVariable($name, $envValues[$name], 'Process')
        }
    }
}

$localMaven = Join-Path $PSScriptRoot '.tools\apache-maven-3.9.9\bin\mvn.cmd'
if (Test-Path $localMaven) {
    & $localMaven spring-boot:run
    exit $LASTEXITCODE
}

if (Get-Command mvn -ErrorAction SilentlyContinue) {
    mvn spring-boot:run
    exit $LASTEXITCODE
}

throw 'Apache Maven is unavailable. Run the setup again or install Maven.'
