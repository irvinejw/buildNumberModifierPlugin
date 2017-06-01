package irvinejw.teamcity.buildNumberModifierPlugin;

import irvinejw.teamcity.buildNumberModifierPlugin.ui.BuildNumberModifierBuildFeature;
import irvinejw.teamcity.buildNumberModifierPlugin.ui.BuildNumberModifierKeyNames;
import jetbrains.buildServer.serverSide.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class BuildNumberModifierListener extends BuildServerAdapter {
    private final BuildNumberModifierKeyNames keyNames = new BuildNumberModifierKeyNames();

    private final SBuildServer buildServer;

    public BuildNumberModifierListener(SBuildServer buildServer) {
        this.buildServer = buildServer;

        this.buildServer.addListener(this);
    }

    @Override
    //public void buildStarted(@NotNull SRunningBuild runningBuild) {
    public void changesLoaded(@NotNull SRunningBuild runningBuild) {
        super.changesLoaded(runningBuild);

        Collection<SBuildFeatureDescriptor> features = runningBuild.getBuildFeaturesOfType(BuildNumberModifierBuildFeature.FEATURE_TYPE);

        if (features != null && !features.isEmpty()) {
            // Should only be one
            SBuildFeatureDescriptor feature = features.iterator().next();
            Branch branch = runningBuild.getBranch();

            if (branch != null && !branch.isDefaultBranch()) {
                // We only attempt to update the build number on non-default branches
                updateBuildNumber(feature, runningBuild, branch);
            }
        }
    }

    private void updateBuildNumber(@NotNull SBuildFeatureDescriptor feature, @NotNull SRunningBuild runningBuild, @NotNull Branch branch) {
        String suffix = feature.getParameters().get(keyNames.getSuffixKey());

        if (suffix != null && !suffix.isEmpty()) {
            if (suffix.contains("{branch}")) {
                suffix = suffix.replaceAll("\\{branch\\}", this.getBranchName(branch));
            }

            if (suffix.contains("{vcsrev}")) {
                suffix = suffix.replaceAll("\\{vcsrev\\}", this.getVcsRevision(runningBuild));
            }

            String buildNumber = runningBuild.getBuildNumber();
            String newBuildNumber = String.format("%s%s", buildNumber, suffix);

            if (newBuildNumber.length() > 256) {
                // Maximum length of a build number after expansions is 256 chars in TeamCity 2017.1
                newBuildNumber = newBuildNumber.substring(0, 256);
            }

            runningBuild.setBuildNumber(newBuildNumber);
        }
    }

    private String getBranchName(Branch branch) {
        String name = branch.getDisplayName();

        if (name != null) {
            if (name.length() > 20) {
                // Build numbers should only be so big.  "20 chars ought to be enough for anybody."
                name = name.substring(0, Math.min(20, name.length() - 1));
            }
        } else {
            name = "";
        }

        return name;
    }

    private String getVcsRevision(SRunningBuild build) {
        // Not available until changesLoaded() occurs
        String revision = build.getParametersProvider().get("build.vcs.number");

        if (revision != null) {
            if (revision.length() == 40) {
                // Probably a Git commit hash, so make it short.
                revision = revision.substring(0, 8);
            }
        } else {
            revision = "???";
        }

        return revision;
    }
}