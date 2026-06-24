[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$PortManifestPath,
    [string]$PortRoot = "05_neoforge_port",
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path $RepoRoot).Path
$portPath = Join-Path $RepoRoot $PortRoot
if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { throw "Port manifest not found: $PortManifestPath" }
if (-not (Test-Path -LiteralPath $portPath -PathType Container)) { throw "Port root not found: $portPath" }
if (-not $RulesRoot) { $RulesRoot = Join-Path (Split-Path -Parent $PSScriptRoot) "rules" }
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_client_server_safety_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_client_server_safety_report.md" }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return "" }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
}
function ConvertTo-RelativePortPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return "" }
    return [System.IO.Path]::GetRelativePath($portPath, $FullPath).Replace("\", "/")
}
function Read-RuleDocument([string]$Root, [string]$FileName) {
    if ([string]::IsNullOrWhiteSpace($Root)) { return [pscustomobject]@{ schemaVersion = 1; entries = @() } }
    $path = Join-Path $Root $FileName
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { return [pscustomobject]@{ schemaVersion = 1; entries = @() } }
    return Get-Content -Raw -LiteralPath $path | ConvertFrom-Json
}
function Normalize-Id([string]$Kind, [string]$Id) {
    if ([string]::IsNullOrWhiteSpace($Id)) { return "" }
    if ($Id.StartsWith("thaumcraft:")) { return $Id }
    return "thaumcraft:$Id"
}
function New-RuleLookup($Document) {
    $lookup = @{}
    foreach ($entry in @($Document.entries)) {
        if ($entry.path) { $lookup["path:$($entry.path)"] = $entry }
        if ($entry.kind -and $entry.id) {
            $kind = [string]$entry.kind
            $id = Normalize-Id $kind ([string]$entry.id)
            $lookup["${kind}:$id"] = $entry
            if ($id.StartsWith("thaumcraft:")) { $lookup["${kind}:$($id.Substring('thaumcraft:'.Length))"] = $entry }
        }
    }
    return $lookup
}
function Get-RuleByPath($Lookup, [string]$Path) {
    foreach ($key in @("path:$Path", "path:$($Path.Replace('\', '/'))")) {
        if ($Lookup.ContainsKey($key)) { return $Lookup[$key] }
    }
    return $null
}
function Get-RuleByEntry($Lookup, [string]$Kind, [string]$RegistryId) {
    $qualified = Normalize-Id $Kind $RegistryId
    foreach ($key in @("${Kind}:$qualified", "${Kind}:$RegistryId")) {
        if ($Lookup.ContainsKey($key)) { return $Lookup[$key] }
    }
    return $null
}
function Get-RuleReason($Rule) {
    if ($null -eq $Rule) { return "" }
    if ($Rule.reason) { return [string]$Rule.reason }
    return "Reviewed client/server safety equivalence rule"
}
function Test-RuleAccepts($Rule, [string]$Finding) {
    if ($null -eq $Rule -or -not $Rule.acceptedFindings) { return $false }
    return @($Rule.acceptedFindings | ForEach-Object { [string]$_ }) -contains $Finding
}
function Format-List($Values) {
    $items = @($Values | Where-Object { $_ } | Sort-Object -Unique)
    if ($items.Count -eq 0) { return "<none>" }
    return $items -join ", "
}
function Get-RegexValues([string]$Text, [string[]]$Patterns) {
    if ([string]::IsNullOrWhiteSpace($Text)) { return @() }
    $values = [System.Collections.Generic.List[string]]::new()
    foreach ($pattern in $Patterns) {
        foreach ($match in [regex]::Matches($Text, $pattern, [System.Text.RegularExpressions.RegexOptions]::IgnoreCase -bor [System.Text.RegularExpressions.RegexOptions]::Singleline)) {
            if ($match.Groups["value"].Success -and -not [string]::IsNullOrWhiteSpace($match.Groups["value"].Value)) {
                $values.Add($match.Groups["value"].Value)
            } else {
                $values.Add($match.Value.Trim())
            }
        }
    }
    return @($values | Sort-Object -Unique)
}
function Get-JavaPackage([string]$Text) {
    $m = [regex]::Match($Text, 'package\s+(?<value>[A-Za-z0-9_.]+)\s*;')
    if ($m.Success) { return $m.Groups['value'].Value }
    return ""
}
function Test-ClientPath([string]$RelativePath, [string]$PackageName) {
    $rp = $RelativePath.Replace("\", "/")
    if ($rp -match '(^|/)client(/|$)' -or $rp -match 'src/main/java/thaumcraft/client/' -or $rp -match 'src/main/java/.*/client/') { return $true }
    if ($PackageName -match '(^|\.)client(\.|$)') { return $true }
    return $false
}
function Test-ServerPath([string]$RelativePath, [string]$PackageName) {
    $rp = $RelativePath.Replace("\", "/")
    if ($rp -match '(^|/)server(/|$)' -or $PackageName -match '(^|\.)server(\.|$)') { return $true }
    return $false
}
function Get-SafetySnapshot([string]$Text) {
    $clientImports = Get-RegexValues $Text @(
        '^\s*import\s+(?<value>net\.minecraft\.client[^;]+);',
        '^\s*import\s+(?<value>com\.mojang\.blaze3d[^;]+);',
        '^\s*import\s+(?<value>org\.joml[^;]+);'
    )
    $clientSymbols = Get-RegexValues $Text @(
        '\bMinecraft\.getInstance\s*\(',
        '\bGuiGraphics\b',
        '\bScreen\b',
        '\bMenuScreens\b',
        '\bBlockEntityRenderer\b',
        '\bEntityRenderer\b',
        '\bEntityRenderers\b',
        '\bBlockEntityRenderers\b',
        '\bRenderType\b',
        '\bPoseStack\b',
        '\bMultiBufferSource\b',
        '\bParticleEngine\b',
        '\bLevelRenderer\b',
        '\bItemProperties\b',
        '\bItemBlockRenderTypes\b'
    )
    $distMarkers = Get-RegexValues $Text @(
        '@OnlyIn\s*\(\s*Dist\.CLIENT\s*\)',
        'Dist\.CLIENT',
        'DistExecutor\.[A-Za-z0-9_]+\s*\(',
        'FMLEnvironment\.dist\s*==\s*Dist\.CLIENT'
    )
    $serverImports = Get-RegexValues $Text @(
        '^\s*import\s+(?<value>net\.minecraft\.server[^;]+);',
        '^\s*import\s+(?<value>net\.minecraft\.world\.level\.ServerLevel[^;]+);'
    )
    $serverSymbols = Get-RegexValues $Text @(
        '\bMinecraftServer\b',
        '\bServerLevel\b',
        '\bServerPlayer\b',
        '\bDedicatedServer\b',
        '\bPacketDistributor\b'
    )
    $commonNetworking = Get-RegexValues $Text @(
        '\bCustomPacketPayload\b',
        '\bPayloadRegistrar\b',
        '\bStreamCodec\b',
        '\bClientbound[A-Za-z0-9_]+\b',
        '\bServerbound[A-Za-z0-9_]+\b'
    )
    return [pscustomobject][ordered]@{
        clientImports = @($clientImports)
        clientSymbols = @($clientSymbols)
        distMarkers = @($distMarkers)
        serverImports = @($serverImports)
        serverSymbols = @($serverSymbols)
        commonNetworking = @($commonNetworking)
        hasClientRef = [bool]($clientImports.Count -gt 0 -or $clientSymbols.Count -gt 0)
        hasDistGuard = [bool]($distMarkers.Count -gt 0)
        hasServerRef = [bool]($serverImports.Count -gt 0 -or $serverSymbols.Count -gt 0)
        hasNetworkingRef = [bool]($commonNetworking.Count -gt 0)
    }
}
function Add-Row($Rows, [string]$Scope, [string]$Kind, [string]$Id, [string]$Path, [string]$Subcheck, [string]$Status, [string]$Evidence, $Rule = $null) {
    $Rows.Add([pscustomobject][ordered]@{
        check = "client_server_safety"
        scope = $Scope
        subcheck = $Subcheck
        kind = $Kind
        id = $Id
        status = $Status
        path = $Path
        evidence = $Evidence
        rule = if ($null -eq $Rule) { $null } else { [pscustomobject][ordered]@{ reason = Get-RuleReason $Rule } }
    })
}
function Get-StatusForFinding([string]$Finding, [bool]$IsUnsafe, $Rule) {
    if ($null -ne $Rule -and (Test-RuleAccepts $Rule $Finding)) { return "CLIENT_SERVER_RULE_ACCEPTED" }
    if ($IsUnsafe) { return "CLIENT_SERVER_REVIEW_NEEDED" }
    return "CLIENT_SERVER_EVIDENCE"
}

$port = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$rules = New-RuleLookup (Read-RuleDocument $RulesRoot "client-server-safety-equivalence.json")
$sourceRoot = Join-Path $portPath "src/main/java"
$rows = [System.Collections.Generic.List[object]]::new()
$fileSnapshots = [System.Collections.Generic.List[object]]::new()
$sourceTextByRelativePath = @{}

foreach ($file in @(Get-ChildItem -LiteralPath $sourceRoot -Recurse -Filter "*.java" -File)) {
    $relative = ConvertTo-RelativePortPath $file.FullName
    $text = Get-Content -Raw -LiteralPath $file.FullName
    $sourceTextByRelativePath[$relative] = $text
    $packageName = Get-JavaPackage $text
    $isClientPath = Test-ClientPath $relative $packageName
    $isServerPath = Test-ServerPath $relative $packageName
    $snapshot = Get-SafetySnapshot $text
    $rule = Get-RuleByPath $rules $relative
    $fileSnapshots.Add([pscustomobject][ordered]@{
        path = $relative
        package = $packageName
        isClientPath = $isClientPath
        isServerPath = $isServerPath
        clientImports = @($snapshot.clientImports)
        clientSymbols = @($snapshot.clientSymbols)
        distMarkers = @($snapshot.distMarkers)
        serverImports = @($snapshot.serverImports)
        serverSymbols = @($snapshot.serverSymbols)
        networking = @($snapshot.commonNetworking)
        hasClientRef = $snapshot.hasClientRef
        hasServerRef = $snapshot.hasServerRef
        hasDistGuard = $snapshot.hasDistGuard
        hasNetworkingRef = $snapshot.hasNetworkingRef
    })
    if ($snapshot.hasClientRef -and -not $isClientPath) {
        $status = Get-StatusForFinding "client_ref_in_common" $true $rule
        $evidence = "clientRefs=$(Format-List @($snapshot.clientImports + $snapshot.clientSymbols)); distGuards=$(Format-List $snapshot.distMarkers)"
        Add-Row $rows "file" "source" "" $relative "client_ref_in_common" $status $evidence $rule
    } elseif ($snapshot.hasClientRef -and $isClientPath) {
        $evidence = "client file contains client refs=$(Format-List @($snapshot.clientImports + $snapshot.clientSymbols))"
        Add-Row $rows "file" "source" "" $relative "client_ref_in_client_path" "CLIENT_SERVER_EVIDENCE" $evidence $rule
    }
    if ($snapshot.hasDistGuard -and -not $isClientPath) {
        $status = Get-StatusForFinding "dist_guard_in_common" $false $rule
        Add-Row $rows "file" "source" "" $relative "dist_guard_in_common" $status "distGuards=$(Format-List $snapshot.distMarkers)" $rule
    }
    if ($snapshot.hasServerRef -and $isClientPath) {
        $status = Get-StatusForFinding "server_ref_in_client_path" $true $rule
        Add-Row $rows "file" "source" "" $relative "server_ref_in_client_path" $status "serverRefs=$(Format-List @($snapshot.serverImports + $snapshot.serverSymbols))" $rule
    } elseif ($snapshot.hasServerRef -and -not $isClientPath) {
        Add-Row $rows "file" "source" "" $relative "server_ref_evidence" "CLIENT_SERVER_EVIDENCE" "serverRefs=$(Format-List @($snapshot.serverImports + $snapshot.serverSymbols))" $rule
    }
    if ($snapshot.hasNetworkingRef) {
        Add-Row $rows "file" "source" "" $relative "networking_boundary_evidence" "CLIENT_SERVER_EVIDENCE" "networkingRefs=$(Format-List $snapshot.commonNetworking)" $rule
    }
}

foreach ($entry in @($port.entries | Sort-Object kind, registryId)) {
    if (-not $entry.portClassFile) { continue }
    $relative = [string]$entry.portClassFile
    if (-not $sourceTextByRelativePath.ContainsKey($relative)) { continue }
    $text = $sourceTextByRelativePath[$relative]
    $packageName = Get-JavaPackage $text
    $isClientPath = Test-ClientPath $relative $packageName
    $snapshot = Get-SafetySnapshot $text
    $rule = Get-RuleByEntry $rules ([string]$entry.kind) ([string]$entry.registryId)
    if ($snapshot.hasClientRef -and -not $isClientPath) {
        $status = Get-StatusForFinding "entry_client_ref_in_common" $true $rule
        Add-Row $rows "entry" ([string]$entry.kind) "thaumcraft:$($entry.registryId)" $relative "entry_client_ref_in_common" $status "registered entry class has client refs outside client package" $rule
    }
    if ($snapshot.hasDistGuard) {
        Add-Row $rows "entry" ([string]$entry.kind) "thaumcraft:$($entry.registryId)" $relative "entry_dist_guard_evidence" "CLIENT_SERVER_EVIDENCE" "registered entry class contains dist guard: $(Format-List $snapshot.distMarkers)" $rule
    }
}

$orderedRows = @($rows | Sort-Object status, scope, path, id, subcheck)
$summaryBySubcheck = @($orderedRows | Group-Object subcheck | Sort-Object Name | ForEach-Object {
    [pscustomobject][ordered]@{
        subcheck = $_.Name
        rows = $_.Count
        evidence = @($_.Group | Where-Object status -eq "CLIENT_SERVER_EVIDENCE").Count
        reviewNeeded = @($_.Group | Where-Object status -eq "CLIENT_SERVER_REVIEW_NEEDED").Count
        ruleAccepted = @($_.Group | Where-Object status -eq "CLIENT_SERVER_RULE_ACCEPTED").Count
    }
})
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    selectedChecks = @("client_server_safety")
    policy = "Report-only client/server safety scan. It records client-only references outside client packages, server references inside client packages, dist guards and networking boundary evidence. It does not change runtime behavior."
    summary = [ordered]@{
        rows = $orderedRows.Count
        evidence = @($orderedRows | Where-Object status -eq "CLIENT_SERVER_EVIDENCE").Count
        reviewNeeded = @($orderedRows | Where-Object status -eq "CLIENT_SERVER_REVIEW_NEEDED").Count
        ruleAccepted = @($orderedRows | Where-Object status -eq "CLIENT_SERVER_RULE_ACCEPTED").Count
        filesScanned = @($fileSnapshots).Count
        bySubcheck = @($summaryBySubcheck)
    }
    files = @($fileSnapshots | Sort-Object path)
    results = $orderedRows
}
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputMarkdown) | Out-Null
$report | ConvertTo-Json -Depth 14 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block client-server safety report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($report.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Rows | Evidence | Review needed | Rule accepted | Files scanned |")
$lines.Add("|---:|---:|---:|---:|---:|")
$lines.Add("| $($report.summary.rows) | $($report.summary.evidence) | $($report.summary.reviewNeeded) | $($report.summary.ruleAccepted) | $($report.summary.filesScanned) |")
$lines.Add("")
$lines.Add("## By subcheck")
$lines.Add("")
$lines.Add("| Subcheck | Rows | Evidence | Review needed | Rule accepted |")
$lines.Add("|---|---:|---:|---:|---:|")
foreach ($row in $summaryBySubcheck) { $lines.Add("| $($row.subcheck) | $($row.rows) | $($row.evidence) | $($row.reviewNeeded) | $($row.ruleAccepted) |") }
$lines.Add("")
$lines.Add("## Review-needed rows")
$lines.Add("")
$lines.Add("| Scope | Kind | ID | Subcheck | Status | Path | Evidence |")
$lines.Add("|---|---|---|---|---|---|---|")
foreach ($row in $orderedRows | Where-Object { $_.status -eq "CLIENT_SERVER_REVIEW_NEEDED" }) {
    $safeEvidence = $row.evidence.Replace("|", "\|")
    $lines.Add("| $($row.scope) | $($row.kind) | ``$($row.id)`` | $($row.subcheck) | $($row.status) | ``$($row.path)`` | $safeEvidence |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM
Write-Output "Client/server safety report: $OutputMarkdown"
Write-Output "Rows=$($report.summary.rows), evidence=$($report.summary.evidence), reviewNeeded=$($report.summary.reviewNeeded), ruleAccepted=$($report.summary.ruleAccepted), files=$($report.summary.filesScanned)"
