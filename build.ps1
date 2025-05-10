# Set the MAVEN_OPTS environment variable
[System.Environment]::SetEnvironmentVariable('MAVEN_OPTS', '-Duser.timezone=Asia/Kolkata', [System.EnvironmentVariableTarget]::Process)

# Load .env file
Get-Content .env | ForEach-Object {
    if ($_ -match "=") {
        $name, $value = $_ -split '=', 2
        # Check if name or value is empty
        if ($name.Trim() -ne "" -and $value.Trim() -ne "") {
            [System.Environment]::SetEnvironmentVariable($name.Trim(), $value.Trim())
        } else {
            Write-Warning "Skipping invalid line: $_"
        }
    } else {
        Write-Warning "Skipping non-formatted line: $_"
    }
}

$env:MAVEN_OPTS="-Duser.timezone=Asia/Kolkata"
mvn clean install

