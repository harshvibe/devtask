
$ErrorActionPreference = "Stop"

# Load variables from .env
$envFile = Join-Path $PSScriptRoot ".env"

if (-not (Test-Path $envFile)) {
    throw ".env file not found in $PSScriptRoot"
}

Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()

    if ($line -and -not $line.StartsWith("#")) {
        $parts = $line -split "=", 2

        if ($parts.Count -eq 2) {
            $name = $parts[0].Trim()
            $value = $parts[1].Trim()

            [Environment]::SetEnvironmentVariable(
                $name, $value, "Process"
            )
        }
    }
}

# Verify required variables
if (-not $env:DB_URL -or
    -not $env:DB_USERNAME -or
    -not $env:DB_PASSWORD) {
    throw "DB_URL, DB_USERNAME, or DB_PASSWORD is missing in .env"
}

# Start Spring Boot
.\mvnw.cmd spring-boot:run
