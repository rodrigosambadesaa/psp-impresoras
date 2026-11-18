# Java runtime security (Java4Shell / CVE-2026-114938)

Java4Shell is a vulnerability in affected OpenJDK **HotSpot** JVM releases,
not in this repository's Java source or Maven dependencies. This repository's
GitHub Actions workflow temporarily disables the vulnerable optimized Base64
decoder during Java 21 compilation and tests.

## Required action when running this code elsewhere

Use a **vendor-confirmed patched JDK/JRE** (for example, the fixed Java 21
security releases) on any machine that builds or executes this application.
For systems which cannot be updated immediately, disable the vulnerable
intrinsic on **every** affected JVM and restart the processes:

- Linux/macOS: `export JAVA_TOOL_OPTIONS=-XX:-UseBASE64Intrinsics`
- PowerShell: `$env:JAVA_TOOL_OPTIONS = '-XX:-UseBASE64Intrinsics'`

Confirm with `java -XX:+PrintFlagsFinal -version`, checking that
`UseBASE64Intrinsics = false`. Setting this environment variable for CI
**does not** configure users' locally installed JVMs or deployed services.

References: [CERT-EU advisory](https://www.cert.europa.eu/publications/security-advisories/2026-021/),
[Eclipse Adoptium guidance](https://adoptium.net/news/2026/11/temurin-november-2026-cspu-java4shell/).
