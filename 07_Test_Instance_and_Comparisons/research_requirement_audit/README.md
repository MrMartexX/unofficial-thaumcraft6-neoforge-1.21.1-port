# Research Requirement Audit

This folder stores the reproducible NeoForge-side research requirement audit for the current port branch.

Run from `05_neoforge_port`:

```powershell
.\gradlew.bat runServer --no-daemon -PtcResearchRequirementAudit=true "-PtcResearchRequirementAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_requirement_audit\thaumcraft_1_21_research_requirements.md" -PtcResearchRequirementAuditDetailLimit=200
```

The server writes the Markdown report after real data reload, logs the summary, then halts automatically. The report distinguishes:

- unresolved requirement identities;
- registry-resolved bridge or placeholder identities that still need subsystem-owned gameplay semantics.

Treat the generated report as an audit artifact. Regenerate it after changing research requirement mapping, registered ids used by research stages, or placeholder/bridge policy.
