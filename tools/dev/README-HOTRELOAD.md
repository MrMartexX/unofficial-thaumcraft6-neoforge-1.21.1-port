# Fast Code Iteration & Hot-Reload for Thaumcraft 6 Port

This guide explains how to iterate on Java code in real-time without restarting Minecraft.

## 1. Quick Class Recompile
To compile changed Java files without doing a full build:
```powershell
powershell -File tools/dev/quick-reload-class.ps1
```
Or run continuous recompile (Gradle watches files and recompiles immediately on save):
```powershell
powershell -File tools/dev/quick-reload-class.ps1 -Continuous
```

## 2. In-Game JVM Hot-Swapping (JDWP port 5005)
When running the client with debug enabled:
1. Run client with `-PtcDebug=true`:
   ```cmd
   cd 05_neoforge_port && gradlew.bat runClient -PtcDebug=true
   ```
2. In your IDE (IntelliJ, VS Code, Eclipse), attach a remote JVM debugger to `localhost:5005`.
3. When you edit any Java method body and save, the IDE will re-compile and hot-swap the bytecode into the running Minecraft client in ~1 second.

## 3. Quick-Relaunch Agent
For fast process relaunching, the project can also leverage the supervisor setup in `D:\MC-HotRestart\Probe`.
