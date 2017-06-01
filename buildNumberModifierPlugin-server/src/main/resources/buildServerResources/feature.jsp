<%@ include file="/include-internal.jsp"%>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>

<jsp:useBean id="keys" class="irvinejw.teamcity.buildNumberModifierPlugin.ui.BuildNumberModifierKeyNames"/>

<l:settingsGroup title="Settings">
    <tr>
        <th>Suffix:<l:star/></th>
        <td>
            <props:textProperty name="${keys.suffixKey}" className="mediumField"/>
            <span class="error" id="error_${keys.suffixKey}"></span>
            <span class="smallNote">Specify suffix for build numbers on non-default branches.  You can use TeamCity properties and the special {branch} and {vcsrev} macros.</span>
        </td>
    </tr>
</l:settingsGroup>
