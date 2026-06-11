# Research data parity audit

This audit treats the original TC6 `assets/thaumcraft/research/*.json` files as the authoritative entry data.

It verifies two independent paths:

1. legacy JSON against the NeoForge `data/thaumcraft/research` resource copy;
2. legacy JSON against the normalized data produced by the running NeoForge parser.

The runtime export also executes pure checks for the exact legacy stage advancement and research-warp split behavior.

## Run

From `05_neoforge_port`:

```powershell
.\gradlew.bat runServer --no-daemon `
  -PtcResearchDataAudit=true `
  -PtcResearchDataAuditPath=../07_Test_Instance_and_Comparisons/research_data_parity/thaumcraft_1_21_research_data.json
```

From the workspace root:

```powershell
python .\07_Test_Instance_and_Comparisons\research_data_parity\tools\compare_research_data.py `
  --legacy .\02_existing_decompiled_repo\Thaumcraft-6-Source-Code-master\src\main\resources\assets\thaumcraft\research `
  --modern .\05_neoforge_port\src\main\resources\data\thaumcraft\research `
  --runtime .\07_Test_Instance_and_Comparisons\research_data_parity\thaumcraft_1_21_research_data.json `
  --legacy-categories .\07_Test_Instance_and_Comparisons\research_data_parity\legacy_research_categories.json `
  --report .\07_Test_Instance_and_Comparisons\research_data_parity\research_data_parity.md `
  --diff .\07_Test_Instance_and_Comparisons\research_data_parity\research_data_diff.json
```
