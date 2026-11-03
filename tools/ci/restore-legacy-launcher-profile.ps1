param([switch]$InstallJei)
$ErrorActionPreference = 'Stop'
$root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$instance = Join-Path $root '07_Test_Instance_and_Comparisons/01_Classic Thaumcraft 1.12.2 Forge/minecraft-instance'
$minecraft = Join-Path $env:APPDATA '.minecraft'
$java = Join-Path $minecraft 'runtime/jre-legacy/windows/jre-legacy/bin/javaw.exe'
$version = '1.12.2-forge-14.23.5.2860'
foreach ($path in @($java, (Join-Path $minecraft "versions/$version/$version.json"), (Join-Path $instance 'mods/Thaumcraft-1.12.2-6.1.BETA26.jar'), (Join-Path $instance 'mods/Baubles-1.12-1.5.2.jar'))) {
    if (!(Test-Path -LiteralPath $path)) { throw "Missing prerequisite: $path" }
}
if ($InstallJei) {
    $release = Invoke-RestMethod 'https://api.modrinth.com/v2/version/jzn0bjty'
    if ('1.12.2' -notin $release.game_versions -or 'forge' -notin $release.loaders) { throw 'Unexpected JEI release' }
    $file = @($release.files | Where-Object primary)[0]
    if (!$file -or $file.filename -ne 'jei_1.12.2-4.16.1.302.jar') { throw 'Unexpected JEI filename' }
    $destination = Join-Path $instance "mods/$($file.filename)"
    $temporary = "$destination.download"
    Invoke-WebRequest $file.url -OutFile $temporary
    if ((Get-FileHash -LiteralPath $temporary -Algorithm SHA512).Hash.ToLowerInvariant() -ne $file.hashes.sha512) { throw 'JEI hash mismatch; download not installed' }
    Move-Item -LiteralPath $temporary -Destination $destination -Force
}
$profilesPath = Join-Path $minecraft 'launcher_profiles.json'
$profiles = Get-Content -LiteralPath $profilesPath -Raw | ConvertFrom-Json
$id = 'thaumcraft6-legacy-reference'
$profile = [pscustomobject]@{
    name = 'Thaumcraft 6 - Legacy reference 1.12.2'
    type = 'custom'
    created = [DateTime]::UtcNow.ToString('o')
    lastVersionId = $version
    gameDir = $instance
    javaDir = $java
    javaArgs = '-Xms512M -Xmx4G'
    icon = 'Bookshelf'
}
$existing = $profiles.profiles.PSObject.Properties[$id]
if ($existing -and $existing.Value.created) { $profile.created = $existing.Value.created }
$profiles.profiles | Add-Member -NotePropertyName $id -NotePropertyValue $profile -Force
Copy-Item -LiteralPath $profilesPath -Destination "$profilesPath.backup-$(Get-Date -Format yyyyMMdd-HHmmssfff)"
$temporaryProfiles = "$profilesPath.tc-new"
[IO.File]::WriteAllText($temporaryProfiles, ($profiles | ConvertTo-Json -Depth 100), [Text.UTF8Encoding]::new($false))
$null = Get-Content -LiteralPath $temporaryProfiles -Raw | ConvertFrom-Json
Move-Item -LiteralPath $temporaryProfiles -Destination $profilesPath -Force
Write-Output "Restored profile: $($profile.name)"
Write-Output "Instance: $instance"
Write-Output 'Restart Minecraft Launcher, then select this installation. Existing saves are preserved.'
