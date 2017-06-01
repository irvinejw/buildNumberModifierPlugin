package irvinejw.teamcity.buildNumberModifierPlugin.ui;

import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.intellij.openapi.util.text.StringUtil.isEmptyOrSpaces;

public class BuildNumberModifierBuildFeature extends BuildFeature {
    public static final String FEATURE_TYPE = "irvinejw.teamcity.buildNumberModifier";

    private final BuildNumberModifierKeyNames keyNames = new BuildNumberModifierKeyNames();

    private final PluginDescriptor descriptor;

    public BuildNumberModifierBuildFeature(@NotNull final PluginDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @NotNull
    @Override
    public String getType() {
        return FEATURE_TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Modify build number on non-branch builds";
    }

    @Nullable
    @Override
    public String getEditParametersUrl()
    {
        return descriptor.getPluginResourcesPath("feature.html");
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull Map<String, String> parameters) {
        return String.format("Build numbers on non-default branches will be suffixed with %s",
                parameters.get(keyNames.getSuffixKey()));
    }

    @Override
    public boolean isMultipleFeaturesPerBuildTypeAllowed() {
        return false;
    }

    @Nullable
    @Override
    public Map<String, String> getDefaultParameters() {
        final Map<String, String> map = new HashMap<String, String>();
        final BuildNumberModifierKeyNames keyNames = new BuildNumberModifierKeyNames();

        map.put(keyNames.getSuffixKey(), "-{vcsrev}");

        return map;
    }

    @Nullable
    @Override
    public PropertiesProcessor getParametersProcessor() {

        return new PropertiesProcessor() {
            private void validate(@NotNull final Map<String, String> properties,
                                  @NotNull final String key,
                                  @NotNull final String message,
                                  @NotNull final Collection<InvalidProperty> results) {

                if (isEmptyOrSpaces(properties.get(key))) {
                    results.add(new InvalidProperty(key, message));
                }
            }

            @Override
            public Collection<InvalidProperty> process(Map<String, String> map) {
                final Collection<InvalidProperty> results = new ArrayList<InvalidProperty>();

                if (map != null)
                {
                    this.validate(map, keyNames.getSuffixKey(), "Suffix must be specified", results);
                }

                return results;
            }
        };
    }
}
