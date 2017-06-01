# buildNumberModifierPlugin
Appends a suffix to a TeamCity build number for builds from a branch other than the VCS root's configured default branch.

Standard TeamCity properties can be used, and the special `{branch}` and `{vcsrev}` macros.  `{branch}` will be replaced
with the display name of the branch and truncated to at most 20 characters.  `{vcsrev}` will be replaced with the VCS
revision number used for the build; if it looks like a Git commit hash, it will be turned into a short hash (8 characters).

## Building

 1. Clone the repo
 2. Run `mvn package`

## Using

 1. Upload `target/buildNumberModifierPlugin.zip` to TeamCity and restart
 2. Add the `Modify build number on non-branch builds` build feature to a build configuration and run a build from a non-default branch
