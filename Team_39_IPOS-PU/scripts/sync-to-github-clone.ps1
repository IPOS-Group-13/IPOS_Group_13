param(
    [Parameter(Mandatory = $true)]
    [string] $TargetPath
)

$ErrorActionPreference = "Stop"
$SourcePath = Split-Path -Parent $PSScriptRoot

$dest = $TargetPath.Trim().TrimEnd('\', '/')
if (-not (Test-Path $dest)) {
    Write-Error "Target does not exist: $dest"
}

Write-Host "Source: $SourcePath"
Write-Host "Target: $dest"
Write-Host ""
Write-Host "Using /E (no /MIR) so files only in the clone (e.g. .git) are NOT deleted."
Write-Host "Excluding: target, .idea, db.properties.local"
Write-Host ""

$args = @(
    $SourcePath,
    $dest,
    "/E",
    "/FFT",
    "/R:2",
    "/W:2",
    "/XD", "target", ".idea",
    "/XF", "db.properties.local",
    "/NFL", "/NDL", "/NJH"
)

$code = Start-Process -FilePath "robocopy.exe" -ArgumentList $args -Wait -PassThru -NoNewWindow
if ($code.ExitCode -ge 8) {
    Write-Error "robocopy failed with exit code $($code.ExitCode)"
}
Write-Host "robocopy exit $($code.ExitCode) (0-7 = success with copy stats)"
Write-Host "Done. In the clone folder run: git status"
