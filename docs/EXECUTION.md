# Add-on execution

The first prototype fixes the bounded wrapper-model defect with atomic aliases
to exact installed resources. The runtime stays inactive if any required
wrapper or target model is absent.

Before running Gradle gates, initialize both exact source submodules, activate
a Python 3.11 or newer virtual environment, and install the development-only
toolkit:

```bash
git submodule update --init --recursive -- \
  tooling/bluemap-addon-toolkit modules/bluemap-addon-adapter-api
python -m pip install --disable-pip-version-check --no-deps \
  --require-hashes --only-binary=:all: \
  --requirement requirements/toolkit.txt
```

The requirement locks the 20,585-byte `v0.3.0-alpha.1` wheel at SHA-256
`82f1ec53603646849a7c2d4b58f3fb7000413fe83043a302bee88cc88daeb8f7`.

## Prototype

Acquire and verify the exact candidate JARs outside Git. Their Gradle
properties are:

- `-PsupplementariesJar=/path/to/supplementaries-neoforge-1.21.1-3.8.5.jar`
- `-PmoonlightJar=/path/to/moonlight-neoforge-1.21.1-3.3.0.jar`

Then run:

```bash
gradle --no-daemon -PbluemapSourcePath=../bluemap-backport \
  <exact-candidate-properties> clean prototypeCheck build
bash gallery/package.sh /tmp/supplementaries-gallery.zip
```

Deploy that JAR and gallery only to disposable staging, verify the intended
BlueMap link loads, and compare it with the matching client. Iterate from
observed defects until the owner explicitly accepts one exact staging JAR.

## Acceptance and release

The migration candidate records the production JAR, sources JAR, POM, and
Gradle module identities under `candidate_artifacts`. After visual acceptance,
change the provenance status to `owner-accepted-release-candidate` and record
the exact integration run and accepted JAR under `owner_accepted_staging`.

Promote `addon_version` through a pull request, clear every remaining prototype
placeholder, and run with all exact candidate properties:

```bash
gradle --no-daemon -PbluemapSourcePath=../bluemap-backport \
  <exact-candidate-properties> -PreleaseTag=v0.1.0-alpha.2 \
  clean build generatePomFileForAddonPublication \
  generateMetadataFileForAddonPublication verifyReleaseCandidate
```

Merge only after owner acceptance and final-head CI pass this gate. Create an annotated
`v<version>` tag at reviewed `main`; the release workflow independently checks
the tag, exact BlueMap checkout, accepted bytes and draft assets before making
the prerelease public. Publication never deploys to production.
